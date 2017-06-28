package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	_ "strconv"
	"fmt"
	_ "github.com/gpmgo/gopm/modules/errors"
	_ "math/big"
	"encoding/json"

)


var logger = shim.NewLogger("hospitalLogger")

type InsuranceChainCode struct {
}

type InsuranceDetail struct {
	UserID string
	Policies []PolicyDetail

}
type PolicyDetail struct {
	ID string
	ExpenseRate string
	Amount float64
}
//
func (t *InsuranceChainCode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	_, args := stub.GetFunctionAndParameters()

	var err error

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	var insOjb InsuranceDetail
	var content string = args[0] //{"userID":"3702821982", "policies":[{"id":"new20170012", "expenseRate":"0.8", "amount":0}]}
	err = json.Unmarshal([]byte(content), &insOjb)
	if err != nil {
		return shim.Error("Fail to unmarshal json data!")
	}

	var insMap = make(map[string]InsuranceDetail) 	//map[string]InsuranceDetail{}
	// usrMapdataBytes is []byte
	//insMapdataBytes, err := stub.GetState(insOjb.UserID)
	//if len(insMapdataBytes) != 0 {
	//	jsonErr := json.Unmarshal(insMapdataBytes, insMap)
	//	if jsonErr != nil {
	//		return shim.Error("Failed to Unmarshal!")
	//	}
	//}
	insMap[insOjb.UserID] = insOjb

	//insMapJson is []byte
	insMapJson, err := json.Marshal(insMap)
	if  err != nil {
		return shim.Error("Failed to Marshal!")
	}
	err = stub.PutState(insOjb.UserID, insMapJson)

	//insMapdata := map[string]InsuranceDetail{}  json.Unmarshal(insMapJson, insMap)
	//fmt.Println("******* Marshal ins obj: %s:   ")
	testBytes, err := stub.GetState(insOjb.UserID)
	fmt.Println("******* : ", string(testBytes))

	return shim.Success([]byte("success!"))
}

func (t*InsuranceChainCode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	if function == "query" {
		return t.query(stub, args)
	}
	return shim.Error(`invalid invoke function name: "invoke" "query"`)
	//return shim.Success(nil)
}


func (t*InsuranceChainCode) query(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var err error

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting name of the user insurance info to query")
	}

	userID := args[0]
	Contentbytes, err := stub.GetState(userID)

	if err != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + userID + "\"}"
		return shim.Error(jsonResp)
	}
	//if Contentbytes == nil {
	//	jsonResp := "{\"Error\":\"Nil amount for " + userID + "\"}"
	//	return shim.Error(jsonResp)
	//}

	jsonResp := string(Contentbytes)
	fmt.Printf("Query Response:%s\n", jsonResp)
	return shim.Success(Contentbytes)
}

func main() {
	err := shim.Start(new(InsuranceChainCode))
	if err != nil {
		fmt.Printf("Error starting Hospital chaincode: %s", err)
		logger.Error("Could not start InsuranceChainCode")
	} else {
		logger.Info("Sucessfully start InsuranceChainCode")
	}
}