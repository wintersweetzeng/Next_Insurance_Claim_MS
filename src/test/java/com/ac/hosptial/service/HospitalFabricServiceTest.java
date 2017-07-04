package com.ac.hosptial.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ac.common.fabric.ChannelWapper;
import com.ac.common.fabric.SmartContractWapper;
import com.ac.common.fabric.model.ChainCodeResultModel;
import com.ac.hosptial.model.MedicineDetailModel;

/**
 * Created by zhenchao.bi on 6/27/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HospitalFabricServiceTest {

	@Autowired
	private HospitalFabricService service;

	@Autowired
	private SmartContractWapper smartContractWapper;

	@Autowired
	private ChannelWapper channel;

	// @Test
	public void testInstall() throws Exception {
		System.out.println("***********************install smartcontract Start***********************");
		smartContractWapper.installHospitalSC();
		System.out.println("***********************install smartcontract End***********************");
	}

	// @Test
	public void testInstant() throws Exception {
		System.out.println("***********************instant smartcontract Start***********************");
		smartContractWapper.instantHospitalSC();
		System.out.println("***********************instant smartcontract End***********************");
	}

	//@Test
	public void testInvoke() {
		System.out.println("***********************invoke smartcontract Start***********************");
		List<MedicineDetailModel> medicines = new ArrayList<>();
		MedicineDetailModel detail1 = new MedicineDetailModel();
		detail1.setId("1000");
		detail1.setName("med1000");
		detail1.setNumber(10);
		detail1.setPrice(10);
		MedicineDetailModel detail2 = new MedicineDetailModel();
		detail2.setId("2000");
		detail2.setName("med2000");
		detail2.setNumber(10);
		detail2.setPrice(20);
		MedicineDetailModel detail3 = new MedicineDetailModel();
		detail3.setId("3000");
		detail3.setName("med3000");
		detail3.setNumber(10);
		detail3.setPrice(30);

		medicines.add(detail1);
		medicines.add(detail2);
		medicines.add(detail3);

		try {
			service.save(medicines, "3702821982");
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("***********************invoke smartcontract end***********************");
	}

	@Test
	public void testQuery() {
		System.out.println("***********************query smartcontract Start***********************");

		try {
			ChainCodeResultModel result = channel.query(SmartContractWapper.HOSPITAL_CHAINCODE_ID,
					SmartContractWapper.peers, "query", new String[] { "3702821982" });

			if (CollectionUtils.isNotEmpty(result.getSuccessful())) {
				result.getSuccessful().forEach(responseBody -> {
					String body = responseBody.getProposalResponse().getResponse().getPayload().toStringUtf8();
					System.out.println(body);
				});
			}
		} catch (InvalidArgumentException | ProposalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("***********************query smartcontract End***********************");
	}
}
