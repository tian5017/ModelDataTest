package model.test;

public class RcsOrderModel{

    private Long orderId;
    private Long webuserId;
    private Long useCoupon;
    private Long promotionPrice;
    private Long wareTotalPrice;
    private Long orderPrice;
    private Long totalFreightFee;
    private Long freightDiscount;
    private String deviceId;
    private String userIp;
    private String consigneeName;
    private String phone;
    private String addressPrefix;
    private String addressDetail;
    private String register;
    private String loginOrderTime;
    //截断后的地址前缀和地址详情
    private String addressPrefixNew;
    private String addressDetailNew;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getWebuserId() {
        return webuserId;
    }

    public void setWebuserId(Long webuserId) {
        this.webuserId = webuserId;
    }

    public Long getUseCoupon() {
        return useCoupon;
    }

    public void setUseCoupon(Long useCoupon) {
        this.useCoupon = useCoupon;
    }

    public Long getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(Long promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public Long getWareTotalPrice() {
        return wareTotalPrice;
    }

    public void setWareTotalPrice(Long wareTotalPrice) {
        this.wareTotalPrice = wareTotalPrice;
    }

    public Long getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Long orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressPrefix() {
        return addressPrefix;
    }

    public void setAddressPrefix(String addressPrefix) {
        this.addressPrefix = addressPrefix;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public Long getFreightDiscount() {
        return freightDiscount;
    }

    public void setFreightDiscount(Long freightDiscount) {
        this.freightDiscount = freightDiscount;
    }

    public String getLoginOrderTime() {
        return loginOrderTime;
    }

    public void setLoginOrderTime(String loginOrderTime) {
        this.loginOrderTime = loginOrderTime;
    }

    public String getAddressPrefixNew() {
        return addressPrefixNew;
    }

    public void setAddressPrefixNew(String addressPrefixNew) {
        this.addressPrefixNew = addressPrefixNew;
    }

    public String getAddressDetailNew() {
        return addressDetailNew;
    }

    public void setAddressDetailNew(String addressDetailNew) {
        this.addressDetailNew = addressDetailNew;
    }

    public Long getTotalFreightFee() {
        return totalFreightFee;
    }

    public void setTotalFreightFee(Long totalFreightFee) {
        this.totalFreightFee = totalFreightFee;
    }
}
