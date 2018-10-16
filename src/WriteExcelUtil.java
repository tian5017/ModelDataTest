import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.util.StringUtils;


public class WriteExcelUtil<T> {

	/**
	 * 
	 * @param sheetTitle
	 *            sheet名称
	 * @param headers
	 *            列表标题
	 * @param dataset
	 *            内容
	 * @param out
	 */
	// public void exportExcel(String sheetTitle, String[] headers, String[]
	// columns, Collection<T> dataset,
	// OutputStream out, String datePattern) {
	// exportExcelByColumn(sheetTitle, headers, columns, dataset, out, datePattern);
	// }

	/**
	 * 导出 xls格式Excel HSSF
	 * @param title
	 * @param headers
	 * @param columns
	 * @param dataset
	 * @param out
	 * @param pattern
	 */
	public void exportHSExcelByColumn(String title, String[] headers, String[] columns, Collection<T> dataset,
			OutputStream out, String pattern) {
		Workbook workbook = new SXSSFWorkbook();
		// 生成一个表格
		Sheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为20个字节
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(24);
		// 生成一个 表格标题行样式
		CellStyle style = workbook.createCellStyle();
		// 设置这些样式
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);
		// 生成一个字体
		Font font = workbook.createFont();
		font.setColor(IndexedColors.WHITE.getIndex());
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		// font.setBoldweight((short)700));
		// 把字体应用到当前的样式
		style.setFont(font);

		// 生成并设置另一个样式 内容的背景
		CellStyle style2 = workbook.createCellStyle();
		style2.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderTop(BorderStyle.THIN);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		// 生成另一个字体
		Font font2 = workbook.createFont();
		font.setBold(true);
		// font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		// 把字体应用到当前的样式
		style2.setFont(font2);

		// 声明一个画图的顶级管理器
		Drawing<?> patriarch = sheet.createDrawingPatriarch();
		// 定义注释的大小和位置
		 Comment comment = patriarch.createCellComment(new HSSFClientAnchor(0, 0, 0,
		 0, (short)4, 2, (short)6, 5));
		// 设置注释内容
//		 comment.setString(new HSSFRichTextString("Created By Phil"));
//		// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
//		 comment.setAuthor("phil");

		// 产生表格标题行
		Row row = sheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellStyle(style);
			RichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}
		
		if(StringUtils.isEmpty(pattern)) {
			pattern = "yyyy/MM/dd";
		}
		FastDateFormat instance = FastDateFormat.getInstance(pattern);
		// 遍历集合数据，产生数据行
		Iterator<T> it = dataset.iterator();
		int index = 0;
		int count = 0;
		while (it.hasNext()) {
			index++;
			row = sheet.createRow(index);
			T t = (T) it.next();
			// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
			// Field[] fields = t.getClass().getDeclaredFields();
			count = headers.length < columns.length ? headers.length : columns.length;
			for (int i = 0; i < count; i++) {
				Cell cell = row.createCell(i);
				cell.setCellStyle(style2);
				String fieldName = columns[i];
				String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				try {
					Class<? extends Object> tCls = t.getClass();
					Method getMethod = tCls.getMethod(getMethodName, new Class[] {});
					Object value = getMethod.invoke(t, new Object[] {});
					// 判断值的类型后进行强制类型转换
					String textValue = null;
					if (value instanceof Date) {
						Date date = (Date) value;
						textValue = instance.format(date);
					} else if (value instanceof byte[]) {
						// 有图片时，设置行高为60px;
						row.setHeightInPoints(60);
						// 设置图片所在列宽度为80px,注意这里单位的一个换算
						sheet.setColumnWidth(i, (short) (35.7 * 80));
						// sheet.autoSizeColumn(i);
						byte[] bsValue = (byte[]) value;
						ClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 6, index, (short) 6, index);
						anchor.setAnchorType(AnchorType.MOVE_DONT_RESIZE);
						patriarch.createPicture(anchor, workbook.addPicture(bsValue, SXSSFWorkbook.PICTURE_TYPE_JPEG));
					} else {
						// 其它数据类型都当作字符串简单处理
//						if (value != null) {
//							textValue = value.toString();
//							if (textValue.equalsIgnoreCase("VLD")) {
//								textValue = "有效";
//							} else if (textValue.equalsIgnoreCase("IVD")) {
//								textValue = "无效";
//							}
//						} else {
//							textValue = "";
//						}
					}
					// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
					if (textValue != null) {
						Pattern p = Pattern.compile("^//d+(//.//d+)?$");
						Matcher matcher = p.matcher(textValue);
						if (matcher.matches()) {
							// 是数字当作double处理
							cell.setCellValue(Double.parseDouble(textValue));
						} else {
							RichTextString richString = new HSSFRichTextString(textValue);
							Font font3 = workbook.createFont();
							font3.setColor(IndexedColors.BLACK.index); // 内容
							richString.applyFont(font3);
							cell.setCellValue(richString);
						}
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} 
			}
		}
		try {
			workbook.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(workbook);
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * 导出 xlsx格式Excel XSSF
	 * @param title
	 * @param headers
	 * @param columns
	 * @param dataset
	 * @param out
	 * @param pattern
	 */
	public void exportXSExcelByColumn(String title, String[] headers, String[] columns,
			Collection<Map<String, Object>> dataset, OutputStream out, String pattern) {
		Workbook workbook = new SXSSFWorkbook();
		// 生成一个表格
		Sheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为20个字节
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(24);
		// 生成一个 表格标题行样式
		CellStyle style = workbook.createCellStyle();
		// 设置这些样式
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);
		// 生成一个字体
		Font font = workbook.createFont();
		font.setColor(IndexedColors.WHITE.getIndex());
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		// font.setBoldweight((short)700));
		// 把字体应用到当前的样式
		style.setFont(font);

		// 生成并设置另一个样式 内容的背景
		CellStyle style2 = workbook.createCellStyle();
		style2.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderTop(BorderStyle.THIN);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		// 生成另一个字体
		Font font2 = workbook.createFont();
		font.setBold(true);
		// font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		// 把字体应用到当前的样式
		style2.setFont(font2);

		// 声明一个画图的顶级管理器
		Drawing<?> patriarch = sheet.createDrawingPatriarch();
		// 定义注释的大小和位置
		 Comment comment = patriarch.createCellComment(new XSSFClientAnchor(0, 0, 0,
		 0, (short)4, 2, (short)6, 5));
		 //设置注释内容
//		 comment.setString(new XSSFRichTextString("Created By Phil"));
//		// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
//		 comment.setAuthor("phil");

		// 产生表格标题行
		Row row = sheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellStyle(style);
			RichTextString text = new XSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}
		if(StringUtils.isEmpty(pattern)) {
			pattern = "yyyy/MM/dd";
		}
		FastDateFormat instance = FastDateFormat.getInstance(pattern);
		// 遍历集合数据，产生数据行
		Iterator<Map<String, Object>> it = dataset.iterator(); // 多个Map集合
		int index = 0;
		int count = 0;
		while (it.hasNext()) {
			index++;
			row = sheet.createRow(index);
			Map<String, Object> map = it.next();
			count = headers.length < columns.length ? headers.length : columns.length;
			for (int i = 0; i < count; i++) {
				Cell cell = row.createCell(i);
				cell.setCellStyle(style2);
				try {
					Object value = map.get(columns[i]);
					// 判断值的类型后进行强制类型转换
					String textValue = null;
					if (value instanceof Date) {
						Date date = (Date) value;
						textValue = instance.format(date);
					} else if (value instanceof byte[]) {
						// 有图片时，设置行高为60px;
						row.setHeightInPoints(60);
						// 设置图片所在列宽度为80px,注意这里单位的一个换算
						sheet.setColumnWidth(i, (short) (35.7 * 80));
						// sheet.autoSizeColumn(i);
						byte[] bsValue = (byte[]) value;
						ClientAnchor anchor = new XSSFClientAnchor(0, 0, 1023, 255, (short) 6, index, (short) 6, index);
						anchor.setAnchorType(AnchorType.MOVE_DONT_RESIZE);
						patriarch.createPicture(anchor, workbook.addPicture(bsValue, Workbook.PICTURE_TYPE_JPEG));
					} else {
						// 其它数据类型都当作字符串简单处理
						if (value != null) {
							textValue = value.toString();
							// if (textValue.equalsIgnoreCase("VLD")) {
							// textValue = "有效";
							// } else if (textValue.equalsIgnoreCase("IVD")) {
							// textValue = "无效";
							// }
						} else {
							textValue = "";
						}
					}
					// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
					if (textValue != null) {
						Pattern p = Pattern.compile("^//d+(//.//d+)?$");
						Matcher matcher = p.matcher(textValue);
						if (matcher.matches()) {
							// 是数字当作double处理
							cell.setCellValue(Double.parseDouble(textValue));
						} else {
							RichTextString richString = new XSSFRichTextString(textValue);
							Font font3 = workbook.createFont();
							font3.setColor(IndexedColors.BLACK.index); // 内容
							richString.applyFont(font3);
							cell.setCellValue(richString);
						}
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			workbook.write(out);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(workbook);
			IOUtils.closeQuietly(out);
		}
	}
}