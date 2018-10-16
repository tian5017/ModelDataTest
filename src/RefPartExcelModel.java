import lombok.Data;

@Data
public class RefPartExcelModel {
	
	    @ExcelColumn("id")
	    private String orderId;

		@ExcelColumn("webuser_id")
		private String webuserId;

		@ExcelColumn("deviceid")
		private String deviceid;
		
		@ExcelColumn("phone")
		private String phone;

		public String getWebuserId() {
			return webuserId;
		}

		public void setWebuserId(String webuserId) {
			this.webuserId = webuserId;
		}

		public String getDeviceid() {
			return deviceid;
		}

		public void setDeviceid(String deviceid) {
			this.deviceid = deviceid;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		
		
		
	}