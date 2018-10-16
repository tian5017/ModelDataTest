import lombok.Data;

@Data
public class RefPartExcel {

		@ExcelColumn("level")
		private String level;

		@ExcelColumn("risktype")
		private String risktype;

		@ExcelColumn("registry_time")
		private String registryTime;
		
		@ExcelColumn("delivery_phone")
		private String deliveryPhone;

		@ExcelColumn("order_created_time")
		private String orderCreatedTime;
		
		@ExcelColumn("user_ip")
		private String userIp;

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		public String getRisktype() {
			return risktype;
		}

		public void setRisktype(String risktype) {
			this.risktype = risktype;
		}

		public String getRegistryTime() {
			return registryTime;
		}

		public void setRegistryTime(String registryTime) {
			this.registryTime = registryTime;
		}

		public String getDeliveryPhone() {
			return deliveryPhone;
		}

		public void setDeliveryPhone(String deliveryPhone) {
			this.deliveryPhone = deliveryPhone;
		}

		public String getOrderCreatedTime() {
			return orderCreatedTime;
		}

		public void setOrderCreatedTime(String orderCreatedTime) {
			this.orderCreatedTime = orderCreatedTime;
		}

		public String getUserIp() {
			return userIp;
		}

		public void setUserIp(String userIp) {
			this.userIp = userIp;
		}
		
		
	}