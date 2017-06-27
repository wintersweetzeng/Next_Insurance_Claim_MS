package com.ac.hosptial.service;

import com.ac.common.constant.SmartContractConstant;
import com.ac.common.fabric.ChannelWapper;
import com.ac.common.fabric.model.ChainCodeResultModel;
import com.ac.hosptial.model.ExpenseDetailModel;
import com.ac.hosptial.model.MedicineDetailModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by zhenchao.bi on 6/26/2017.
 */
@Service
public class HospitalFabricServiceImpl implements HospitalFabricService {

    @Autowired
    private ChannelWapper channel;

    private ObjectMapper mapper = new ObjectMapper();

    /*
    *{"uid":"3702821982","expenseTime":"20001010010203","claimed":false,
    * "medicines":[{"name":"med1000","id":"1000","number":10,"price":10},
    * {"name":"med2000","id":"2000","number":10,"price":20},
    * {"name":"med3000","id":"3000","number":10,"price":30}]}
     */
    @Override
    public void save(List<MedicineDetailModel> medicineDetailList, String userId) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("User id is empty!");
        }

        ExpenseDetailModel detail = new ExpenseDetailModel();
        detail.setUid(userId);
        detail.setClaimed(false);
        detail.setExpenseTime(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        detail.setMedicines(medicineDetailList);

        String json = mapper.writeValueAsString(detail);

        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(SmartContractConstant.Hospital.CHAINCODE_NAME)
                .setVersion(SmartContractConstant.Hospital.CHAINCODE_VERSION)
                .setPath(SmartContractConstant.Hospital.CHAINCODE_PATH)
                .build();

        //ChaincodeID chaincodeID, Collection<Peer> peers, String invokeMethod,String[] invokeArgs
        ChainCodeResultModel result = channel.transationProposal(chaincodeID, channel.getAllPeers(), "invoke", new String[]{json});

        if (CollectionUtils.isEmpty(result.getFailed())) {
            CompletableFuture<BlockEvent.TransactionEvent> future = channel.transationSubmit(result.getSuccessful());
            future.whenComplete((event, exception) -> {
                if (exception != null) {
                    throw new IllegalArgumentException(exception.getMessage());
                }

                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            });
        } else {
            throw new IllegalArgumentException("Failed!!!");
        }

    }


}
