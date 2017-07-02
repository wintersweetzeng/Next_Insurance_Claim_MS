package com.ac.hosptial.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ac.common.fabric.ChannelWapper;
import com.ac.common.fabric.SmartContractWapper;
import com.ac.common.fabric.model.ChainCodeResultModel;
import com.ac.hosptial.model.ExpenseDetailModel;
import com.ac.hosptial.model.MedicineDetailModel;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by zhenchao.bi on 6/26/2017.
 */
@Service
public class HospitalFabricServiceImpl implements HospitalFabricService {

	@Autowired
	private ChannelWapper channel;

	private ObjectMapper mapper = new ObjectMapper();

	/*
	 * {"uid":"3702821982","expenseTime":"20001010010203","claimed":false,
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

		ChainCodeResultModel result = channel.transationProposal(SmartContractWapper.HOSPITAL_CHAINCODE_ID,
				SmartContractWapper.peers, "invoke", new String[] { json });

		if (CollectionUtils.isEmpty(result.getFailed())) {
			CompletableFuture<BlockEvent.TransactionEvent> future = channel.transationSubmit(result.getSuccessful());
			// future.whenComplete((event, exception) -> {
			// if (exception != null) {
			// throw new IllegalArgumentException(exception.getMessage());
			// }
			//
			// });
		} else {
			// throw new IllegalArgumentException("Failed!!!");
		}

	}

	@Override
	public String query(String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
