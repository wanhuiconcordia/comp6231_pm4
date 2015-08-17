package tools.message;

public enum Action {
	ACK, INIT, REPLICA_RESULT, HEART_BEAT,
	sync, askSync, doSync,
	signUp, signIn, getCatelog, submitOrder,
	shippingGoods, getProducts, getProductsByRegisteredManufacturers,
	getProductsByID, getProductsByType, processPurchaseOrder, getProductInfo, receivePayment, getProductList, getName,
	test
}
