package main

import (
	_ "bytes"
	_ "encoding/binary"
	"fmt"
	"github.com/hyperledger/fabric/common/util"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	_ "strconv"

	_ "github.com/gpmgo/gopm/modules/cli"
	"encoding/json"
)

var logger = shim.NewLogger("CustomerLogger")

type CustomerChainCode struct {
}

type MedicineDetail struct {
	Id     string
	Name   string
	Price  int
	Number int
}

type ExpenseDetail struct {
	Uid string
	//yyyyMMddHHmmss
	ExpenseTime string
	Claimed     bool
	Medicines   []MedicineDetail
}

type CustomerDetail struct {
	UserID  string
	Status  string
	Claimed bool
	Amount  float64
}

func (t *CustomerChainCode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	_, args := stub.GetFunctionAndParameters()

	var err error

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	var cusObj CustomerDetail
	var content string = args[0] //{"UserID":"3702821982","Status":"initialized","Claimed":false,"Amount":0}
	err = json.Unmarshal([]byte(content), &cusObj)
	if err != nil {
		return shim.Error("Fail to unmarshal json data!")
	}


	cusJson, err := json.Marshal(cusObj)
	if  err != nil {
		return shim.Error("Failed to Marshal!")
	}
	err = stub.PutState(cusObj.UserID, cusJson)

	testBytes, err := stub.GetState(cusObj.UserID)
	fmt.Println("******* Initial Customer: ", string(testBytes))

	return shim.Success([]byte("success!"))
}

//private methohd
func (t *CustomerChainCode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	if function == "invoke" {
		return t.invoke(stub, args)
	} else if function == "query" {
		return t.query(stub, args)
	}
	return shim.Error(`invalid invoke function name: "invoke" "query"`)
}

func (t *CustomerChainCode) invoke(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 3 {
		return shim.Error("Incorrect number of arguments. Expecting name of the user insurance info to query")
	}

	chainCodeToCall := args[0]
	channelID := args[1]
	userID := args[2]

	//var  queryArgs  = [][]byte {[]byte("query"), []byte(t.UserID)}    ---This is another constuct arges methods
	q := "query"
	queryArgs := util.ToChaincodeArgs(q, userID)
	response := stub.InvokeChaincode(chainCodeToCall, queryArgs, channelID)

	// usrMapdataBytes is []byte
	usrMapdataBytes := response.GetPayload()

	var usrMapdata map[string]ExpenseDetail

	if usrMapdataBytes != nil {
		logger.Info("*********Invoke Hospital CC from Customer CC result: ", string(usrMapdataBytes))
		//fmt.Println("--------$$$$$$$$: ", string(usrMapdataBytes))
		err := json.Unmarshal(usrMapdataBytes, &usrMapdata)
		if err != nil {
			return shim.Error("Fail to unmarshal json data!")
		}

	}

	//var jsonObj ExpenseDetail
	var totalExpAm float64
	for _, expense := range usrMapdata {
		//expense = usrMapdata[expTime]
		for i := 0; i < len(expense.Medicines); i++ {
			totalExpAm += float64( float64(expense.Medicines[i].Price) * 0.8)
		}
	}

	//buf := new(bytes.Buffer)
	//binary.Write(buf, binary.BigEndian, usrMapdata)

	var custObj CustomerDetail
	custObj.UserID = userID
	custObj.Claimed = true
	custObj.Amount = totalExpAm
	custObj.Status = "Processed"

	custJson, err := json.Marshal(custObj)

	// Write the state to the ledger
	err = stub.PutState(userID, []byte(custJson))
	//testBytes, err := stub.GetState(userID)
	//fmt.Println("******* Put Value: ", string(testBytes))
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

func (t *CustomerChainCode) query(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1 -> user id")
	}

	uid := args[0]
	custBytes, err := stub.GetState(uid)
	if err != nil {
		return shim.Success([]byte("Data is null!"))
	}

	return shim.Success(custBytes)
}

func main() {
	err := shim.Start(new(CustomerChainCode))
	if err != nil {
		fmt.Printf("Error starting Hospital chaincode: %s", err)
		logger.Error("Could not start CustomerChainCode")
	} else {
		logger.Info("Sucessfully start CustomerChainCode")
	}
}

//`{"uid":"3702821982","expenseTime":"20001010010203","claimed":false,"medicines":[{"name":"med1000","id":"1000","number":10,"price":10},{"name":"med2000","id":"2000","number":10,"price":20},{"name":"med3000","id":"3000","number":10,"price":30}]}`
