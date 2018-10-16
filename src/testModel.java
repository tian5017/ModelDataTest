import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.google.common.collect.Maps;


public class testModel {

	private static int threshold = 7;

	public static void main(String[] argv){

		List<Map<String, Object>> datasWrite = new ArrayList<Map<String, Object>>();
		RefPartExcel tmp = new RefPartExcel();
		RefPartExcelModel refPartExcel = new RefPartExcelModel();
		Map<String, Integer> uuidMap = Maps.newHashMap();
		Map<String, Integer> phoneMap = Maps.newHashMap();
		Map<String, Integer> uMap = Maps.newHashMap();
		Map<String, Integer> pMap = Maps.newHashMap();

		Map<String, String> uMap_ = Maps.newHashMap();
		Map<String, String> pMap_ = Maps.newHashMap();

		Map<String, Integer> orderuMap = Maps.newHashMap();

		Map<String, Integer> orderpMap = Maps.newHashMap();

		List<String> resultUuid = new ArrayList<String>();
		List<String> resultPhone = new ArrayList<String>();

		try {
			FileInputStream inputStream = new FileInputStream("C:\\Users\\dmaller\\Desktop\\123\\data-rcs-0817-0820-2-model.xls");
			List<RefPartExcelModel> datas = ReadExcelUtil.readExcel(inputStream, RefPartExcelModel.class);

			for (int i=0; i < datas.size(); i++) {
				refPartExcel = datas.get(i);
				String uuidKey = refPartExcel.getDeviceid() + refPartExcel.getWebuserId();
				String uKey = refPartExcel.getDeviceid();

				if (!uMap.containsKey(uKey)) {
					if (!uuidMap.containsKey(uuidKey)) {
						uuidMap.put(uuidKey, 1);
						uMap.put(uKey, 1);
						uMap_.put(uKey, refPartExcel.getOrderId());
						orderuMap.put(refPartExcel.getOrderId(), 0);

					}
				} else {
					if (!uuidMap.containsKey(uuidKey)) {
						uuidMap.put(uuidKey, 1);
						Integer tNum = ((Integer) uMap.get(uKey));
						uMap.put(uKey, ++tNum);

						String order = ((String) uMap_.get(uKey)) + "&" + refPartExcel.getOrderId();
						uMap_.put(uKey, order);

						orderuMap.put(refPartExcel.getOrderId(), 0);

					} else {
						String order = ((String) uMap_.get(uKey)) + "&" + refPartExcel.getOrderId();
						uMap_.put(uKey, order);
						orderuMap.put(refPartExcel.getOrderId(), 0);

					}
				}

				if (uMap.get(uKey) >= threshold) {
					if (!resultUuid.contains(uKey)) {
						resultUuid.add(uKey);
					}
					String[] orders = uMap_.get(uKey).split("&");
					for (String s : orders) {
						orderuMap.put(s, 1);
					}
				}
			}

			orderpMap = orderuMap;
			for (int i = 0; i < datas.size(); i++) {
				refPartExcel = datas.get(i);
				String phoneKey = refPartExcel.getPhone() + refPartExcel.getWebuserId();
				String pKey = refPartExcel.getPhone();
				if (!pMap.containsKey(pKey)) {
					if (!phoneMap.containsKey(phoneKey)) {
						phoneMap.put(phoneKey, 1);
						pMap.put(pKey, 1);
						pMap_.put(pKey, refPartExcel.getOrderId());

					}
				} else {
					if (!phoneMap.containsKey(phoneKey)) {
						phoneMap.put(phoneKey, 1);
						Integer tNum = ((Integer) pMap.get(pKey));
						pMap.put(pKey, ++tNum);

						String order = ((String) pMap_.get(pKey)) + "&" + refPartExcel.getOrderId();
						pMap_.put(pKey, order);
					} else {
						String order = ((String) pMap_.get(pKey)) + "&" + refPartExcel.getOrderId();
						pMap_.put(pKey, order);
					}
				}

				if (pMap.get(pKey) >= threshold) {
					if (!resultPhone.contains(pKey)) {
						resultPhone.add(pKey);

					}
					String[] orders = pMap_.get(pKey).split("&");
					for (String s : orders) {
						if (0 == orderpMap.get(s)) {
							orderpMap.put(s, 1);
						}
					}
				}

			}

			Iterator iterator = orderpMap.entrySet().iterator();

			while (iterator.hasNext()) {

				Entry entry = (Entry) iterator.next();

				Object key = entry.getKey();
				Map<String, Object> temp_ = new HashMap<>();
				temp_.put("orderid", ((String) entry.getKey()).substring(0, 11));
				temp_.put("flag", entry.getValue());
				datasWrite.add(temp_);

			}

			// Map<String, Object> temp_ = new HashMap<>();
			//
			//
			// temp_.put("level", "null");
			// temp_.put("riskType", "null");
			// temp_.put("delivery_phone", "");
			// datasWrite.add(temp_);

			File file = new File("C:\\Users\\dmaller\\Desktop\\123\\data-rcs-0817-0820-2-model-over.xls");
			FileOutputStream out = new FileOutputStream(file);

			WriteExcelUtil<List<Map<String, Object>>> exportExcel = new WriteExcelUtil<>();

			exportExcel.exportXSExcelByColumn("Title", new String[] { "orderid", "flag" },
					new String[] { "orderid", "flag" }, datasWrite, out, null);

			System.out.println("done");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}