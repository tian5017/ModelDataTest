import lombok.Data;

@Data
public class RefPartExcelTongji {

		@ExcelColumn("phone_no")
		private String phoneNo;

		@ExcelColumn("client_ip")
		private String clientIp;

		@ExcelColumn("created_time")
		private String createTime;

		public String getPhoneNo() {
			return phoneNo;
		}

		public void setPhoneNo(String phoneNo) {
			this.phoneNo = phoneNo;
		}

		public String getClientIp() {
			return clientIp;
		}

		public void setClientIp(String clientIp) {
			this.clientIp = clientIp;
		}

		public String getCreateTime() {
			return createTime;
		}

		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		

		
	}