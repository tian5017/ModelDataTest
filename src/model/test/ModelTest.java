package model.test;

import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class ModelTest {

    //模型常数，用来控制时间窗口下的拦截规则（如：7天7单后进行拦截）
    private static double MODEL_N1 = 4.0;
    private static double MODEL_N2 = 3.0;


    public static List<String[]> getFeatureByData(List<RcsOrderModel> rcsOrderModelList) {
        List<String[]> resultStringList = new ArrayList<>();
        try {
            for(int i=0; i<rcsOrderModelList.size(); i++){
                System.out.println(i);
                List<String> tmpStringList = new ArrayList<>();
                RcsOrderModel rcsOrderModel = rcsOrderModelList.get(i);
                //x1: round(a.use_coupon/a.ware_total_price,2) coupon_radio
                String param_1 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(rcsOrderModel.getUseCoupon().doubleValue()
                        / rcsOrderModel.getWareTotalPrice().doubleValue(), 2));
                tmpStringList.add(param_1);
                //x2：round(a.promotion_price/a.ware_total_price,2) promotion_radio
                String param_2 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(rcsOrderModel.getPromotionPrice().doubleValue()
                        / rcsOrderModel.getWareTotalPrice().doubleValue(), 2));
                tmpStringList.add(param_2);
                //x3: round((a.ware_total_price-a.use_coupon-a.promotion_price)/a.ware_total_price,2) realpay_radio
                String param_3 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans((rcsOrderModel.getWareTotalPrice().doubleValue()
                        - rcsOrderModel.getUseCoupon().doubleValue() - rcsOrderModel.getPromotionPrice().doubleValue())
                        / rcsOrderModel.getWareTotalPrice().doubleValue(), 2));
                tmpStringList.add(param_3);
                //x4: login_order_time 登录到下单时间   20180813  田育林增加
                //计算方法：从订单创建时间开始，算出前推20万秒的时间，根据此订单的user_id和上一步算出的时间作为查询条件，
                //到表dmall_rcs_workbench.user_login_device_info中查询登录时间，若查到则用时间差除200000秒，若没查到，则为1
                String param_4 = String.valueOf(rcsOrderModel.getLoginOrderTime());
                if(param_4.equals("null")){
                    param_4 = "1.0";
                }
                tmpStringList.add(param_4);
                /****************************取出相同地址前缀相关的信息*****************************/
                //当前订单的地址前缀
                StringBuilder addressPrefixClean = ModelInvokerToolKit.cleaningAfterAddressPrefix(rcsOrderModel.getAddressPrefix());
                //获取和当前地址前缀有相同前缀的所有数据
                List<RcsOrderModel> sameAddressPrefixDataList = getSameAddressPrefixData(rcsOrderModelList, addressPrefixClean.toString(), i);
                int same_prefix_num = sameAddressPrefixDataList.size() == 0 ? 1 : sameAddressPrefixDataList.size();
                //取出相同地址前缀下的所有的前缀+详细地址信息
                List<String> addressPrefixDetails = new ArrayList<>();
                //取出相同地址前缀下的所有的详细地址信息
                List<String> addressDetails = new ArrayList<>();
                //取出相同地址前缀下的所有的用户ID（user_id）
                List<String> addressUserIds = new ArrayList<>();
                //取出相同地址前缀下的所有的用户名
                List<String> addressNames = new ArrayList<>();
                //取出相同地址前缀下的所有的收获手机号
                List<String> addressPhones = new ArrayList<>();
                //取出相同地址前缀下的所有的设备号
                List<String> addressUuids = new ArrayList<>();
                //取出相同地址前缀下的所有的用户IP地址
                List<String> addressUserIps = new ArrayList<>();
                if(!sameAddressPrefixDataList.isEmpty() && sameAddressPrefixDataList.size()>0){
                    for(RcsOrderModel sapd : sameAddressPrefixDataList){
                        addressPrefixDetails.add(StringUtils.isBlank(sapd.getAddressDetail()) ? addressPrefixClean.toString() : addressPrefixClean.toString() + sapd.getAddressDetail());
                        addressDetails.add(StringUtils.isBlank(sapd.getAddressDetail()) ? "" : sapd.getAddressDetail());
                        addressUserIds.add(StringUtils.isBlank(String.valueOf(sapd.getWebuserId())) ? "" : String.valueOf(sapd.getWebuserId()));
                        addressNames.add(StringUtils.isBlank(sapd.getConsigneeName()) ? "" : sapd.getConsigneeName());
                        addressPhones.add(StringUtils.isBlank(sapd.getPhone()) ? "" : sapd.getPhone());
                        addressUuids.add(StringUtils.isBlank(sapd.getDeviceId()) ? "" : sapd.getDeviceId());
                        addressUserIps.add(StringUtils.isBlank(sapd.getUserIp()) ? "" : sapd.getUserIp());
                    }
                }
                /******************************************************************************************/
                //x5: doc_simil 地址（前缀+详细地址）相似度    20180813  田育林增加,通过编辑距离计算
                String param_5 = String.valueOf(0);
                if(!addressPrefixDetails.isEmpty() && addressPrefixDetails.size()>0){
                    Double address_simil = ModelInvokerToolKit.getDocListSimilByOne(addressPrefixDetails, addressPrefixClean.toString()+rcsOrderModel.getAddressDetail());
                    param_5 = String.valueOf(address_simil);
                }
                tmpStringList.add(param_5);
                //x6: real_freight_fee = (total_freight_fee - discount_freight_fee) / total_freight_fee
                String param_6 = String.valueOf(0);
                double total_freight_fee = rcsOrderModel.getTotalFreightFee().doubleValue();
                double discount_freight_fee = rcsOrderModel.getFreightDiscount().doubleValue();
                if(total_freight_fee!=0 && (total_freight_fee-discount_freight_fee)>=0){
                    param_6 = String.valueOf((total_freight_fee - discount_freight_fee) / total_freight_fee);
                }
                tmpStringList.add(param_6);
                //x7: 姓是否在百家姓中 xingconfidence   //x8: 判断名字的长度是否是2到3个字 nameconfidence   //x9: 判断姓和名是否相同 xingmingxiangdeng
                String param_7 = "0";
                String param_8 = "0";
                String param_9 = "0";
                String name = rcsOrderModel.getConsigneeName();
                if(name.length() > 0){
                    if (ModelInvokerToolKit.isBelongsFamilyNames(rcsOrderModel.getConsigneeName().substring(0, 1))) {
                        param_7 = "1";
                    }
                    if(name.length() == 2 || name.length() == 3){
                        param_8 = "1";
                    }
                    if(name.length() > 1 && !StringUtils.equals(name.substring(0, 1), name.substring(1, 2))){
                        param_9 = "1";
                    }
                }
                tmpStringList.add(param_7);
                tmpStringList.add(param_8);
                tmpStringList.add(param_9);
                //x10: (order_price - 1999 / 1999)  price_ratio
                String param_10 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans((rcsOrderModel.getOrderPrice().doubleValue() - 1999.0) / 1999.0, 9));
                tmpStringList.add(param_10);
                //x11: sameaddressprefix_sameIP_ratio （相同地址前缀的订单中相同用户IP数量-5）/（相同地址前缀订单的数量-4）
                String param_11 = String.valueOf(0);
                if(!addressUserIps.isEmpty() && addressUserIps.size()>0){
                    //和当前用户IP相同的IP数量
                    int tmp_diff = ModelInvokerToolKit.getSameListNumByItem(addressUserIps, rcsOrderModel.getUserIp());
                    if(tmp_diff > MODEL_N1 && same_prefix_num > MODEL_N2){
                        double tmp_ratio_userip_order = ModelInvokerToolKit.bigDecimalTrans((tmp_diff - MODEL_N1) / (same_prefix_num - MODEL_N2), 9);
                        param_11 = String.valueOf(tmp_ratio_userip_order <= 0.0 ? "0" : tmp_ratio_userip_order);
                    }
                }
                tmpStringList.add(param_11);
                //x12: sameaddressprefix_similardetail 相同地址前缀下地址详情相似度（数字）
                String param_12 = String.valueOf(0);
                //x13: sameaddressprefix_detail_zhongwen 相同地址前缀下地址详情相似度（汉字和字母）
                String param_13 = String.valueOf(0);
                if(!addressDetails.isEmpty() && addressDetails.size()>0){
                    List<String> word_addressDetails = new ArrayList<>();
                    List<String> num_addressDetails = new ArrayList<>();
                    for(String addressDetail : addressDetails){
                        //提取出地址中的汉字和字母
                        String result_words = ModelInvokerToolKit.filterWords(addressDetail);
                        if(StringUtils.isNotBlank(result_words)){
                            word_addressDetails.add(result_words);
                        }
                        //提取中地址中的数字
                        String result_nums = ModelInvokerToolKit.filterNums(addressDetail);
                        if(StringUtils.isNotBlank(result_nums)){
                            num_addressDetails.add(result_nums);
                        }
                    }
                    Double detail_simil_num = ModelInvokerToolKit.getDocListSimilByOne(num_addressDetails, ModelInvokerToolKit.filterNums(rcsOrderModel.getAddressDetail()));
                    param_12 = String.valueOf(detail_simil_num);
                    Double detail_simil_word = ModelInvokerToolKit.getDocListSimilByOne(word_addressDetails, ModelInvokerToolKit.filterWords(rcsOrderModel.getAddressDetail()));
                    param_13 = String.valueOf(detail_simil_word);
                }
                tmpStringList.add(param_12);
                tmpStringList.add(param_13);
                //x14: sameaddressprefix_userratio （相同地址前缀的订单中不同的用户数量-5）/（相同地址前缀订单的数量-4）
                String param_14 = String.valueOf(0);
                if(!addressUserIds.isEmpty() && addressUserIds.size()>0){
                    //list转换为set，去除list中的重复元素
                    Set<String> addressUserIdSet = new HashSet<>(addressUserIds);
                    if(addressUserIdSet.size()>MODEL_N1 && same_prefix_num>MODEL_N2){
                        double tmp_ratio_user_order = ModelInvokerToolKit.bigDecimalTrans((addressUserIdSet.size() - MODEL_N1) / (same_prefix_num - MODEL_N2), 9);
                        param_14 = String.valueOf(tmp_ratio_user_order <= 0.0 ? "0" : tmp_ratio_user_order);
                    }
                }
                tmpStringList.add(param_14);
                //x15: same_address_prefix_name （相同地址前缀下相同的用户名数量-1）/ 10
                String param_15 = String.valueOf(0);
                if(!addressNames.isEmpty() && addressNames.size()>0){
                    int tmp_diff = ModelInvokerToolKit.getSameListNumByItem(addressNames, rcsOrderModel.getConsigneeName());
                    if(tmp_diff > 1){
                        param_15 = String.valueOf((tmp_diff - 1.0) / 10.0);
                    }
                }
                tmpStringList.add(param_15);
                //x16: same_address_prefix_phone （相同地址前缀下相同的收货手机号-1）/ 10
                String param_16 = String.valueOf(0);
                if(!addressPhones.isEmpty() && addressPhones.size()>0){
                    int tmp_diff = ModelInvokerToolKit.getSameListNumByItem(addressPhones, rcsOrderModel.getPhone());
                    if(tmp_diff > 1){
                        param_16 = String.valueOf((tmp_diff - 1.0) / 10.0);
                    }
                }
                tmpStringList.add(param_16);
                //x17: same_address_prefix_deviceid （相同地址前缀下相同的设备号-1）/ 10
                String param_17 = String.valueOf(0);
                if(!addressUuids.isEmpty() && addressUuids.size()>0){
                    int tmp_diff = ModelInvokerToolKit.getSameListNumByItem(addressUuids, rcsOrderModel.getDeviceId());
                    if(tmp_diff > 1){
                        param_17 = String.valueOf((tmp_diff - 1.0) / 10.0);
                    }
                }
                tmpStringList.add(param_17);
                //x18:  注册来源为2、3、4、9定义为1   register
                String register = rcsOrderModel.getRegister();
                String param_18 = ModelInvokerToolKit.isBelongsRiskTypeByRegRcs(register) ? "1" : "0";
                tmpStringList.add(param_18);
                /****************************取出截断后相同地址前缀相关的信息*****************************/
                //x19: sameaddressprefix_new_samedetail 截断地址前缀后，相同地址前缀下的详细地址相似度
                String param_19 = String.valueOf(0);
                String addressPrefix_new = rcsOrderModel.getAddressPrefixNew();
                String addressDetail_new = rcsOrderModel.getAddressDetailNew();
                StringBuilder addressPrefixClean_new = ModelInvokerToolKit.cleaningAfterAddressPrefix(addressPrefix_new);
                //获取和当前地址前缀(截断后)有相同前缀的所有数据
                List<RcsOrderModel> sameAddressPrefixDataList_new = getNewSameAddressPrefixData(rcsOrderModelList, addressPrefixClean_new.toString(), i);
                List<String> addressDetailList_new = new ArrayList<>();
                if (!sameAddressPrefixDataList_new.isEmpty() && sameAddressPrefixDataList_new.size()>0) {
                    for(RcsOrderModel sapd_new : sameAddressPrefixDataList_new){
                        addressDetailList_new.add(StringUtils.isBlank(sapd_new.getAddressDetailNew()) ? "" : sapd_new.getAddressDetailNew());
                    }
                }
                if(!addressDetailList_new.isEmpty() && addressDetailList_new.size()>0){
                    Double address_simil_new = ModelInvokerToolKit.getDocListSimilByOne(addressDetailList_new, addressDetail_new);
                    param_19 = String.valueOf(address_simil_new);
                }
                tmpStringList.add(param_19);
                /*****************************************新增组合特征***********************************************/
                //x20: sameaddressprefix_userratio+sameaddressprefix_new_samedetail
                String param_20 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_14) + Double.parseDouble(param_19), 9));
                tmpStringList.add(param_20);
                //x21: sameaddressprefix_userratio+sameaddressprefix_similardetail
                String param_21 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_14) + Double.parseDouble(param_12), 9));
                tmpStringList.add(param_21);
                //x22: sameaddressprefix_userratio+same_address_prefix_name
                String param_22 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_14) + Double.parseDouble(param_15), 9));
                tmpStringList.add(param_22);
                //x23: sameaddressprefix_new_samedetail-sameaddressprefix_detail_zhongwen
                String param_23 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_19) - Double.parseDouble(param_13), 9));
                tmpStringList.add(param_23);
                //x24: sameaddressprefix_userratio+doc_simil
                String param_24 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_14) + Double.parseDouble(param_5), 9));
                tmpStringList.add(param_24);
                //x25: sameaddressprefix_userratio/price_ratio
                String param_25 = String.valueOf(0);
                if(Double.parseDouble(param_14)!=0 && Double.parseDouble(param_10)!=0){
                    param_25 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_14) / Double.parseDouble(param_10), 9));
                }
                tmpStringList.add(param_25);
                //x26: price_ratio+login_order_time
                String param_26 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_10) + Double.parseDouble(param_4), 9));
                tmpStringList.add(param_26);
                //x27: sameaddressprefix_userratio+coupon_radio
                String param_27 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_14) + Double.parseDouble(param_1), 9));
                tmpStringList.add(param_27);
                //x28: sameaddressprefix_userratio*same_address_prefix_name
                String param_28 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_14) * Double.parseDouble(param_15), 9));
                tmpStringList.add(param_28);
                //x29: sameaddressprefix_new_samedetail/doc_simil
                String param_29 = String.valueOf(0);
                if(Double.parseDouble(param_19)!=0 && Double.parseDouble(param_5)!=0){
                    param_29 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_19) / Double.parseDouble(param_5), 9));
                }
                tmpStringList.add(param_29);
                //x30: sameaddressprefix_new_samedetail-doc_simil
                String param_30 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_19) - Double.parseDouble(param_5), 9));
                tmpStringList.add(param_30);
                //x31: sameaddressprefix_new_samedetail-sameaddressprefix_sameIP_ratio
                String param_31 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_19) - Double.parseDouble(param_11), 9));
                tmpStringList.add(param_31);
                //x32: sameaddressprefix_userratio-login_order_time
                String param_32 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_14) - Double.parseDouble(param_4), 9));
                tmpStringList.add(param_32);
                //x33: sameaddressprefix_userratio-sameaddressprefix_similardetail
                String param_33 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_14) - Double.parseDouble(param_12), 9));
                tmpStringList.add(param_33);
                //x34: sameaddressprefix_similardetail+login_order_time
                String param_34 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_12) + Double.parseDouble(param_4), 9));
                tmpStringList.add(param_34);
                //x35: sameaddressprefix_similardetail-doc_simil
                String param_35 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_12) - Double.parseDouble(param_5), 9));
                tmpStringList.add(param_35);
                //x36: price_ratio+sameaddressprefix_detail_zhongwen
                String param_36 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_10) + Double.parseDouble(param_13), 9));
                tmpStringList.add(param_36);
                //x37: price_ratio+coupon_radio
                String param_37 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_10) + Double.parseDouble(param_1), 9));
                tmpStringList.add(param_37);
                //x38: sameaddressprefix_similardetail+same_address_prefix_name
                String param_38 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_12) + Double.parseDouble(param_15), 9));
                tmpStringList.add(param_38);
                //x39: coupon_radio/sameaddressprefix_sameIP_ratio
                String param_39 = String.valueOf(0);
                if(Double.parseDouble(param_1)!=0 && Double.parseDouble(param_11)!=0){
                    param_39 = String.valueOf(ModelInvokerToolKit.bigDecimalTrans(Double.parseDouble(param_1) / Double.parseDouble(param_11), 9));
                }
                tmpStringList.add(param_39);
                /***************************************************************************************************/
                String[] strings = new String[tmpStringList.size()];
                resultStringList.add(tmpStringList.toArray(strings));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStringList;
    }


    //从CSV文件中读取数据
    public static List<RcsOrderModel> getOrderModelList(List<JSONObject> jsonObjectList) {
        List<RcsOrderModel> rcsOrderModelList = new ArrayList<>();
        if(!jsonObjectList.isEmpty() && jsonObjectList.size()>0){
            for(JSONObject orderJsonObj : jsonObjectList){
                RcsOrderModel orderModel = new RcsOrderModel();
                //封装ordermodel对象
                orderModel.setOrderId(orderJsonObj.getLong("id"));
                orderModel.setWebuserId(orderJsonObj.getLong("webuser_id"));
                orderModel.setUseCoupon(orderJsonObj.getLong("use_coupon"));
                orderModel.setPromotionPrice(orderJsonObj.getLong("promotion_price"));
                orderModel.setWareTotalPrice(orderJsonObj.getLong("total_price"));
                orderModel.setOrderPrice(orderJsonObj.getLong("order_price"));
                orderModel.setTotalFreightFee(orderJsonObj.getLong("total_freight_fee"));
                orderModel.setFreightDiscount(orderJsonObj.getLong("discount_freight_fee"));
                orderModel.setDeviceId(orderJsonObj.getString("deviceid"));
                orderModel.setUserIp(orderJsonObj.getString("user_ip"));
                orderModel.setConsigneeName(orderJsonObj.getString("name"));
                orderModel.setPhone(orderJsonObj.getString("phone"));
                orderModel.setAddressPrefix(orderJsonObj.getString("address_prefix"));
                orderModel.setAddressDetail(orderJsonObj.getString("address_detail"));
                orderModel.setRegister(orderJsonObj.getString("register_src"));
                orderModel.setLoginOrderTime(orderJsonObj.getString("login_order_time"));
                orderModel.setAddressPrefixNew(orderJsonObj.getString("address_prefix_new"));
                orderModel.setAddressDetailNew(orderJsonObj.getString("address_detail_new"));
                rcsOrderModelList.add(orderModel);
            }
        }
        return rcsOrderModelList;
    }

    //获取和当前订单地址前缀相同的所有订单信息
    private static List<RcsOrderModel> getSameAddressPrefixData(List<RcsOrderModel> rcsOrderModelList, String addressPrefix, int a){
        List<RcsOrderModel> returnDataList = new ArrayList<>();
        for(int i=0; i<=a; i++){
            RcsOrderModel rcsOrderModel = rcsOrderModelList.get(i);
            String currAddressPrefix = ModelInvokerToolKit.cleaningAfterAddressPrefix(rcsOrderModel.getAddressPrefix()).toString();
            if(StringUtils.equals(addressPrefix, currAddressPrefix)){
                returnDataList.add(rcsOrderModel);
            }
        }
        return returnDataList;
    }


    //获取截断后和当前订单地址前缀相同的所有订单信息
    private static List<RcsOrderModel> getNewSameAddressPrefixData(List<RcsOrderModel> rcsOrderModelList, String addressPrefixNew, int a){
        List<RcsOrderModel> returnDataList = new ArrayList<>();
        for(int i=0; i<=a; i++){
            RcsOrderModel rcsOrderModel = rcsOrderModelList.get(i);
            String currAddressPrefixNew = ModelInvokerToolKit.cleaningAfterAddressPrefix(rcsOrderModel.getAddressPrefixNew()).toString();
            if(StringUtils.equals(addressPrefixNew, currAddressPrefixNew)){
                returnDataList.add(rcsOrderModel);
            }
        }
        return returnDataList;
    }


    //读取CSV文件
    public static List<JSONObject> csvRead(String filePath){
        List<JSONObject> jsonObjectList = new ArrayList<>();
        try {
            //创建CSV读对象
            CsvReader csvReader = new CsvReader(filePath, ',', Charset.forName("GBK"));
            //保存表头
            String[] header = {};
            while (csvReader.readRecord()){
                JSONObject jsonObject = new JSONObject();
                //把头保存起来
                if(csvReader.getCurrentRecord()==0){
                    header = csvReader.getValues();
                }else{
                    if(header.length > 0){
                        for(int i=0; i<header.length; i++){
                            jsonObject.put(header[i], csvReader.get(i));
                        }
                    }
                    jsonObjectList.add(jsonObject);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObjectList;
    }


    //写CSV文件
    public static void csvWrite(String filePath, String[] headers, List<String[]> dataList){
        try {
            //创建CSV写对象
            CsvWriter csvWriter = new CsvWriter(filePath, ',', Charset.forName("UTF-8"));
            //写表头
            csvWriter.writeRecord(headers);
            //写表数据
            if(!dataList.isEmpty() && dataList.size()>0){
                for(String[] dataArr : dataList){
                    csvWriter.writeRecord(dataArr);
                }
            }
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("over");
    }


    public static void main(String[] args) {
        String filePath = "C:\\Users\\dmaller\\Desktop\\123test\\abcde-sort.csv";
        List<JSONObject> jsonObjectList = csvRead(filePath);
        List<RcsOrderModel> rcsOrderModelList = getOrderModelList(jsonObjectList);
        List<String[]> resultStringList = getFeatureByData(rcsOrderModelList);
        String[] headers = {"coupon_radio", "promotion_radio", "realpay_radio", "login_order_time", "doc_simil", "real_freight_fee",
                "xingconfidence", "nameconfidence", "xingmingxiangdeng", "price_ratio", "sameaddressprefix_sameIP_ratio", "sameaddressprefix_similardetail",
                "sameaddressprefix_detail_zhongwen", "sameaddressprefix_userratio", "same_address_prefix_name", "same_address_prefix_phone",
                "same_address_prefix_deviceid", "register", "sameaddressprefix_new_samedetail", "sameaddressprefix_userratio+sameaddressprefix_new_samedetail",
                "sameaddressprefix_userratio+sameaddressprefix_similardetail", "sameaddressprefix_userratio+same_address_prefix_name",
                "sameaddressprefix_new_samedetail-sameaddressprefix_detail_zhongwen", "sameaddressprefix_userratio+doc_simil", "sameaddressprefix_userratio/price_ratio",
                "price_ratio+login_order_time", "sameaddressprefix_userratio+coupon_radio", "sameaddressprefix_userratio*same_address_prefix_name",
                "sameaddressprefix_new_samedetail/doc_simil", "sameaddressprefix_new_samedetail-doc_simil", "sameaddressprefix_new_samedetail-sameaddressprefix_sameIP_ratio",
                "sameaddressprefix_userratio-login_order_time", "sameaddressprefix_userratio-sameaddressprefix_similardetail", "sameaddressprefix_similardetail+login_order_time",
                "sameaddressprefix_similardetail-doc_simil", "price_ratio+sameaddressprefix_detail_zhongwen", "price_ratio+coupon_radio", "sameaddressprefix_similardetail+same_address_prefix_name",
                "coupon_radio/sameaddressprefix_sameIP_ratio"};
        csvWrite("C:\\Users\\dmaller\\Desktop\\123test\\abcde-train.csv", headers, resultStringList);
    }

}
