package model.test;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import static org.apache.commons.lang3.math.NumberUtils.min;


public class ModelInvokerToolKit {

    private static Map<Character, Integer> numeralsMap = new HashMap<>();

    static{
        numeralsMap.put('零', 0);
        numeralsMap.put('O', 0);
        numeralsMap.put('o', 0);
        numeralsMap.put('一', 1);
        numeralsMap.put('①', 1);
        numeralsMap.put('I', 1);
        numeralsMap.put('i', 1);
        numeralsMap.put('二', 2);
        numeralsMap.put('②', 2);
        numeralsMap.put('两', 2);
        numeralsMap.put('三', 3);
        numeralsMap.put('③', 3);
        numeralsMap.put('四', 4);
        numeralsMap.put('④', 4);
        numeralsMap.put('五', 5);
        numeralsMap.put('⑤', 5);
        numeralsMap.put('六', 6);
        numeralsMap.put('⑥', 6);
        numeralsMap.put('七', 7);
        numeralsMap.put('⑦', 7);
        numeralsMap.put('八', 8);
        numeralsMap.put('⑧', 8);
        numeralsMap.put('九', 9);
        numeralsMap.put('⑨', 9);
        numeralsMap.put('十', 10);
        numeralsMap.put('⑩', 10);
        numeralsMap.put('百', 100);
        numeralsMap.put('千', 1000);
        numeralsMap.put('万', 10000);
        numeralsMap.put('亿', 100000000);
    }


    //模型常数，用来控制时间窗口下的拦截规则（如：7天7单后进行拦截）
    private static double MODEL_N1 = 4.0;
    private static double MODEL_N2 = 3.0;

    public static boolean IsWindowOS() {
        String osName = System.getProperties().getProperty("os.name").toLowerCase();
        return osName.startsWith("windows");
    }


    public static String encodeBase64Str(String plainText) {
        byte[] b = new Base64().encode(plainText.getBytes());
        return new String(b);
    }

    public static String dncodeBase64Str(String plainText) {
        byte[] b = new Base64().decode(plainText.getBytes());
        return new String(b);
    }

    public static boolean isBelongsFamilyNames(String familyName) {
        String[] familyNames = {"赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许", "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", " 戚", "谢", "邹", "喻", "柏", "水", "窦", "章", "云", "苏", "潘", "葛", "奚", "范", "彭", "郎", "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳", "丰", "鲍", "史", "唐", " 费", "廉", "岑", "薛", "雷", "贺", "倪", "汤", "滕", "殷", "罗", "毕", "郝", "邬", "安", "常", "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "平", "黄", " 和", "穆", "萧", "尹", "姚", "邵", "湛", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明", "臧", "计", "伏", "成", "戴", "谈", "宋", "茅", "庞", "熊", "纪", "舒", "屈", "项", "祝", "董", "梁", " 杜", "阮", "蓝", "闵", "席", "季", "麻", "强", "贾", "路", "娄", "危", "江", "童", "颜", "郭", "梅", "盛", "林", "刁", "钟", "徐", "丘", "骆", "高", "夏", "蔡", "田", "樊", "胡", "凌", "霍", " 虞", "万", "支", "柯", "昝", "管", "卢", "莫", "经", "房", "裘", "缪", "干", "解", "应", "宗", "丁", "宣", "贲", "邓", "郁", "单", "杭", "洪", "包", "诸", "左", "石", "崔", "吉", "钮", "龚", " 程", "嵇", "邢", "滑", "裴", "陆", "荣", "翁", "荀", "羊", "於", "惠", "甄", "麴", "家", "封", "芮", "羿", "储", "靳", "汲", "邴", "糜", "松", "井", "段", "富", "巫", "乌", "焦", "巴", "弓", " 牧", "隗", "山", "谷", "车", "侯", "宓", "蓬", "全", "郗", "班", "仰", "秋", "仲", "伊", "宫", "宁", "仇", "栾", "暴", "甘", "钭", "厉", "戌", "祖", "武", "符", "刘", "景", "詹", "束", "龙", " 叶", "幸", "司", "韶", "郜", "黎", "蓟", "薄", "印", "宿", "白", "怀", "蒲", "邰", "从", "鄂", "索", "咸", "籍", "赖", "卓", "蔺", "屠", "蒙", "池", "乔", "阴", "郁", "胥", "能", "苍", "双", " 闻", "莘", "党", "翟", "谭", "贡", "劳", "逢", "姬", "申", "扶", "堵", "冉", "宰", "郦", "雍", "郤", "璩", "桑", "桂", "濮", "牛", "寿", "通", "边", "扈", "燕", "冀", "郏", "浦", "尚", "农", " 温", "别", "庄", "晏", "柴", "瞿", "阎", "充", "慕", "连", "茹", "习", "宦", "艾", "鱼", "容", "向", "古", "易", "慎", "戈", "廖", "庾", "终", "暨", "居", "衡", "步", "都", "耿", "满", "弘", " 匡", "国", "文", "寇", "广", "禄", "阙", "东", "欧", "殳", "沃", "利", "蔚", "越", "菱", "隆", "师", "巩", "厍", "聂", "晃", "勾", "敖", "融", "冷", "訾", "辛", "阚", "那", "简", "饶", "空", " 曾", "毋", "沙", "乜", "养", "鞠", "须", "丰", "巢", "关", "蒯", "相", "查", "后", "荆", "红", "游", "竺", "权", "逯", "盖", "益", "桓", "公", " 万俟", "司马", "上官", "欧阳", "夏侯", "诸葛", "闻人", "东方", "赫连", "皇甫", "尉迟", "公羊", "澹台", "公冶", "宗政", "濮阳", "淳于", "单于", "太叔", " 申屠", "公孙", "仲孙", "轩辕", "令狐", "钟离", "宇文", "长孙", "慕容", "司徒", "司空"};
        return Arrays.asList(familyNames).contains(familyName);
    }

    public static boolean isBelongsRiskTypeByRegRcs(String regRcs) {
        String[] riskRegRcs = {"2", "3", "4", "9"};
        return Arrays.asList(riskRegRcs).contains(regRcs);
    }

    public static StringBuilder cleaningAfterAddressPrefix(String addressPrefix) {
        StringBuilder addressPrefixClean = new StringBuilder();
        for (int i = 0; i < addressPrefix.length(); i++) {
            char tmpChar = addressPrefix.charAt(i);
            if (tmpChar == ' ' || tmpChar == '\r' || tmpChar == '\t' || tmpChar == '\n') {
                continue;
            }

            addressPrefixClean.append(tmpChar);
        }

        return addressPrefixClean;
    }

    /**
     * 把日期格式化为yyyyMMdd
     *
     * @param date
     * @return
     */
    public static String formatDateYMD(Date date) {
        return new DateTime(date).toString("yyyyMMdd");
    }


    /**
     * 四舍五入
     */
    public static double bigDecimalTrans(double data, int keepNums) {
        BigDecimal bg = new BigDecimal(Double.toString(data));
        return bg.setScale(keepNums, RoundingMode.HALF_UP).doubleValue();
    }


    /**
     * 求两两字符串编辑距离
     */
    private static int editDistance(String str1, String str2){
        int d[][]; // 矩阵
        int y = str1.length();
        int x = str2.length();
        char ch1; // str1的
        char ch2; // str2的
        int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (y == 0) {
            return x;
        }
        if (x == 0) {
            return y;
        }
        d = new int[y + 1][x + 1]; // 计算编辑距离二维数组
        for (int j = 0; j <= x; j++) { // 初始化编辑距离二维数组第一行
            d[0][j] = j;
        }
        for (int i = 0; i <= y; i++) { // 初始化编辑距离二维数组第一列
            d[i][0] = i;
        }
        for (int i = 1; i <= y; i++) { // 遍历str1
            ch1 = str1.charAt(i - 1);
            // 去匹配str2
            for (int j = 1; j <= x; j++) {
                ch2 = str2.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[y][x];
    }


    /**
     * 中文数字转为阿拉伯数字，如：五百二十三 --> 523
     * 田育林 20180813
     */
    public static String chineseToDigits(String numStr){
        double result = 0;
        double tmp = 0;
        double hnd_mln = 0;
        for(int i=0; i<numStr.length(); i++){
            char curr_char = numStr.charAt(i);
            int currDigit = numeralsMap.get(curr_char);
            if(currDigit == Math.pow(10, 8)){  //亿
                result = result + tmp;
                result = result * currDigit;
                hnd_mln = hnd_mln * Math.pow(10, 8) + result;
                result = 0;
                tmp = 0;
            }else if(currDigit == Math.pow(10, 4)){  //万
                result = result + tmp;
                result = result * currDigit;
                tmp = 0;
            }else if(currDigit >= 10){   //十, 百, 千
                tmp = (tmp == 0 ? 1 : tmp);
                result = result + currDigit * tmp;
                tmp = 0;
            }else{   //个
                tmp = tmp * 10 + currDigit;
            }
        }
        result = result + tmp + hnd_mln;
        Double result_d = new Double(result);
        return String.valueOf(result_d.longValue());
    }

    /**
     * 将一个中文句子中的中文数字转换为阿拉伯数字，如：“你好五三四” --> “你好534”
     * 田育林 20180813
     */
    public static String chineseNumToArab(String in_str){
        Character[] num_str_start_symbol = {'一', '①', '二', '②', '两', '三', '③', '四', '④', '五', '⑤', '六', '⑥', '七', '⑦', '八', '⑧', '九', '⑨', '十', '⑩', 'O', 'o', 'I', 'i'};
        int in_l = in_str.length();
        String out_str = "";
        if(StringUtils.isBlank(in_str) || in_l == 0){
            return out_str;
        }
        boolean has_num_stat = false;
        String num_str = "";
        for(int idx=0; idx<in_l; idx++){
            if (Arrays.asList(num_str_start_symbol).contains(in_str.charAt(idx))){
                if(!has_num_stat){
                    has_num_stat = true;
                }
                num_str += in_str.charAt(idx);
            }else{
                if(has_num_stat){
                    if(numeralsMap.keySet().contains(in_str.charAt(idx))){
                        num_str += in_str.charAt(idx);
                        continue;
                    }else{
                        String num_result = chineseToDigits(num_str);
                        num_str = "";
                        has_num_stat = false;
                        out_str += num_result;
                    }
                }
                out_str += in_str.charAt(idx);
            }
        }
        if(num_str.length() > 0){
            String num_result = chineseToDigits(num_str);
            out_str += num_result;
        }
        return out_str;
    }


    /**
     * 去掉一个句子中没有意义的汉字（对于地址来说，去掉的无意义汉字比如：号、楼、单元等）
     * 田育林  20180813
     */
    public static String dropStopWordByDoc(String str){
        if(StringUtils.isNotBlank(str)){
            String[] in_stop_arr = {"号", "楼", "单元", "房", "座", "层", "院"};
            for(String in_stop : in_stop_arr){
                str = str.replaceAll(in_stop, "");
            }
        }
        return str;
    }


    /**
     * 计算一个句子在一个句子列表中的相似度
     * 田育林  20180813
     */
    public static Double getDocListSimilByOne(List strs, String strOne){
        Double result_simil = 0.0;
        if(strs!= null && !strs.isEmpty() && strs.size()>0 && StringUtils.isNotBlank(strOne)){
            List<Double> tmp_arr = new ArrayList<>();
            for(int i=0; i<strs.size(); i++){
                String str1_0 = strs.get(i).toString().trim();
                String str2_0 = strOne.trim();
                //将句子中的中文数字转换为阿拉伯数字
                String str1_1 = chineseNumToArab(str1_0);
                String str2_1 = chineseNumToArab(str2_0);
                //提取出字符串中的汉字、字母和数字
                String str1_2 = filterWordsAndNums(str1_1);
                String str2_2 = filterWordsAndNums(str2_1);
                //去掉句子中的无意义的字词
                String str1_3 = dropStopWordByDoc(str1_2);
                String str2_3 = dropStopWordByDoc(str2_2);
                if(Math.max(str1_3.length(), str2_3.length()) == 0){
                    tmp_arr.add(0.0);
                }else{
                    //计算两个地址的编辑距离
                    double editDistance = (double)editDistance(str1_3, str2_3);
                    //通过编辑距离计算相似度
                    tmp_arr.add(1.0 - editDistance / Math.max(str1_3.length(), str2_3.length()));
                }
            }
            if(!tmp_arr.isEmpty() && tmp_arr.size()>0){
                int tmp_count = 0;
                for(double tmp_arr_m : tmp_arr){
                    if(tmp_arr_m >= 0.8){
                        tmp_count += 1;
                    }
                }
                if(tmp_count >= MODEL_N1){
                    result_simil = ModelInvokerToolKit.bigDecimalTrans((tmp_count - MODEL_N1) / (tmp_arr.size() - MODEL_N2), 2);
                }
            }
        }
        return result_simil;
    }


    /**
     * 地址前缀中如果包含括号或者数字，则进行截断，前部分形成新的前缀，后部分合并详细地址形成新的详细地址
     * 田育林  20180814
     */
    public static List<String> getAddressPrefixCut(String addressPrefix, String addressDetail){
        String[] tmp_arr = {"(", "（", "[", "【"};
        List<String> tmp_list = Arrays.asList(tmp_arr);
        String addressPrefix_new = "";
        String addressDetail_new = "";
        for(int i=0; i<addressPrefix.length(); i++){
            char pre_char = addressPrefix.charAt(i);
            //如果前缀中包含括号类的字符或者数字，则进行截断
            if(tmp_list.contains(String.valueOf(pre_char)) || Character.isDigit(pre_char)){
                addressPrefix_new = addressPrefix.substring(0, i);
                addressDetail_new = addressPrefix.substring(i) + addressDetail;
                break;
            }
        }
        if(StringUtils.isBlank(addressPrefix_new)){
            addressPrefix_new = addressPrefix;
        }
        if(StringUtils.isBlank(addressDetail_new)){
            addressDetail_new = addressDetail;
        }
        String[] return_arr = {addressPrefix_new, addressDetail_new};
        return Arrays.asList(return_arr);
    }


    /**
     * 提取句子中的数字
     * 田育林修改  20180814
     */
    public static  String filterNums(String str) {
        String new_str = chineseNumToArab(str);
        StringBuilder result = new StringBuilder();
        if(StringUtils.isNotBlank(new_str)){
            for(int i=0; i<new_str.length(); i++){
                char cur_char = new_str.charAt(i);
                if(Character.isDigit(cur_char)){
                    result.append(String.valueOf(cur_char));
                }
            }
        }
        return result.toString();
    }


    /**
     * 提取句子中的汉字和字母
     * 田育林  20180814
     */
    public static String filterWords(String str) {
        String new_str = chineseNumToArab(str);
        StringBuilder result = new StringBuilder();
        if(StringUtils.isNotBlank(new_str)){
            for(int i=0; i<new_str.length(); i++){
                char cur_char = new_str.charAt(i);
                if(Character.isLetter(cur_char)){
                    result.append(String.valueOf(cur_char));
                }
            }
        }
        return result.toString();
    }


    /**
     * 提取句子中的汉字、字母和数字（舍弃所有的特殊字符）
     * 田育林  20180822
     */
    public static String filterWordsAndNums(String str) {
        String new_str = chineseNumToArab(str);
        StringBuilder result = new StringBuilder();
        if(StringUtils.isNotBlank(new_str)){
            for(int i=0; i<new_str.length(); i++){
                char cur_char = new_str.charAt(i);
                if(Character.isLetter(cur_char) || Character.isDigit(cur_char)){
                    result.append(String.valueOf(cur_char));
                }
            }
        }
        return result.toString();
    }


    /**
     * # 计算data_list中和str_item相等的元素个数
     * 田育林  20180814
     */
    public static int getSameListNumByItem(List<String> data_list, String str_item) {
        int result = 0;
        for(String data : data_list){
            if(StringUtils.equals(str_item, data)){
                result++;
            }
        }
        return result;
    }

}