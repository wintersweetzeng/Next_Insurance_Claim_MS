package com.ac.common.fabric;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ac.common.fabric.config.HospitalInfoConfig;
import com.ac.common.fabric.config.InsuranceInfoConfig;
import com.ac.common.fabric.config.OrgCommonConfig;
import com.ac.common.fabric.hfc.HFCKeyStore;
import com.ac.common.fabric.hfc.HFCUser;
import com.ac.common.fabric.hfc.OrgInfo;
import com.ac.common.fabric.utils.IOUtils;
import com.google.common.collect.Lists;

import lombok.Getter;

@Service
public class OrgWapper {

    @Autowired
    private HospitalInfoConfig hospitalInfoConfig;

    @Autowired
    private InsuranceInfoConfig insuranceInfoConfig;

    @Getter
    private OrgInfo hospitalOrgInfo;

    @Getter
    private OrgInfo insuranceOrgInfo;

    @Autowired
    private HFCKeyStore hfcKeyStore;

    //private ResourceLoader loader = new DefaultResourceLoader();

    @PostConstruct
    private void init() throws Exception {
        insuranceOrgInfo = this.initOrgInfo(insuranceInfoConfig);
        hospitalOrgInfo = this.initOrgInfo(hospitalInfoConfig);
    }

    public List<OrgInfo> getAllOrgInfo() {
        return Lists.newArrayList(hospitalOrgInfo, insuranceOrgInfo);
    }

    private OrgInfo initOrgInfo(OrgCommonConfig config) throws Exception {

        OrgInfo orgInfo = new OrgInfo();

        orgInfo.setId(config.getId());

        String[] ps = config.getPeerLocations().split("[ \t]*,[ \t]*");
        if (ArrayUtils.isEmpty(ps)) {
            throw new IllegalArgumentException("Peers is empty!");
        }

        for (String peer : ps) {
            String[] nl = peer.split("[ \t]*@[ \t]*");
            orgInfo.addPeerLocation(nl[0], grpcTLSify(nl[1]));
        }

        orgInfo.setDomainName(config.getDomname());

        ps = config.getOrdererLocations().split("[ \t]*,[ \t]*");
        if (ArrayUtils.isEmpty(ps)) {
            throw new IllegalArgumentException("Orderer is empty!");
        }

        for (String orderer : ps) {
            String[] nl = orderer.split("[ \t]*@[ \t]*");
            orgInfo.addOrdererLocation(nl[0], grpcTLSify(nl[1]));
        }

        ps = config.getEventhubLocations().split("[ \t]*,[ \t]*");
        for (String event : ps) {
            String[] nl = event.split("[ \t]*@[ \t]*");
            orgInfo.addEventHubLocation(nl[0], grpcTLSify(nl[1]));
        }

        orgInfo.setCaLocation(httpTLSify(config.getCaLocation()));
        //CAproperty
        String caName = String.join("", "ca.", config.getDomname(), "-cert.pem");

        //File caPath = loader.getResource("classpath:/keyStore/" + config.getId() + "/ca/" + caName).getFile();
        File caPath = IOUtils.getFileFromClasspath("/keyStore/" + config.getId() + "/ca/" + caName);
        Properties properties = new Properties();
        properties.setProperty("pemFile", caPath.getAbsolutePath());
        properties.setProperty("allowAllHostNames", "true");
        orgInfo.setCaProperties(properties);

        HFCAClient ca = HFCAClient.createNewInstance(orgInfo.getCaLocation(),
                orgInfo.getCaProperties());
        ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        orgInfo.setCaClient(ca);

        // admin user
        HFCUser admin = hfcKeyStore.getMember(config.getAdmin().getName(), config.getName());
        admin.setEnrollmentSecret(config.getAdmin().getPassword());
        if (!admin.isEnrolled()) {
            // admin.setEnrollment(ca.enroll(admin.getName(), admin.getEnrollmentSecret()));
            admin.setEnrollment(ca.enroll("admin", "adminpw"));
            admin.setMPSID(config.getMspid());
        }
        orgInfo.setAdmin(admin);

        // peerAdmin user
       // File privateKey = loader.getResource(config.getAdmin().getPrivateKey()).getFile();
       // File cert = loader.getResource(config.getAdmin().getPublicKey()).getFile();
        
        File privateKey = IOUtils.getFileFromClasspath(config.getAdmin().getPrivateKey());
        File cert = IOUtils.getFileFromClasspath(config.getAdmin().getPublicKey());

        HFCUser peerOrgAdmin = hfcKeyStore.getMember(config.getName() + "Admin", config.getName(), config.getMspid(),
                privateKey, cert);
        orgInfo.setPeerAdmin(peerOrgAdmin);

        return orgInfo;
    }

    private String grpcTLSify(String location) {
        location = StringUtils.trim(location);
//		Exception e = SDKUtil.checkGrpcUrl(location);
//		if (e != null) {
//			throw new RuntimeException(String.format("Bad TEST parameters for grpc url %s", location), e);
//		}
        return location;
    }

    private String httpTLSify(String location) {
        return StringUtils.trim(location);
    }

}
