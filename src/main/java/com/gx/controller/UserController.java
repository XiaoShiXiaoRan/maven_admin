package com.gx.controller;

import com.gx.annotation.ReturnJsonMapping;
import com.gx.po.SysUser;
import com.gx.service.IUserService;
import com.gx.service.impl.UserServiceImpl;
import com.gx.util.MD5Util;
import com.gx.util.Tools;
import com.gx.vo.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {
    // 上传配置
    private static final String UPLOAD_PATH = "E:/z/javaProjectUp/BaseAdmin/user/";//员工头像上传目录

    //service
    @Autowired
    private IUserService userService;

    /**
     * 页面
     */
    @RequestMapping
    public String index() {
        return "/user";
    }

    /**
     * 查询部门数据 for layuiTree
     */
    @ReturnJsonMapping("/selectDepartmentForTree")
    public List<LayuiTreeVo> selectDepartmentForTree() {
        return this.userService.selectDepartmentForTree();
    }


    /**
     * 分页查询
     */
    @ReturnJsonMapping("/selectPageList")
    public LayuiTableData<UserVo> selectPageList(int page, int limit, Integer departmentId, String userName,
                                                 String realName, String mobile, Integer userStatus, String startEndDate) {
        //开始时间和结束时间   2021-04-22 - 2021-05-22
        String startDate = null;
        String endDate = null;
        if (Tools.isNotNull(startEndDate)) {
            String[] strDates = startEndDate.split(" - ");
            if (strDates.length == 2) {
                if (strDates[0].matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                    startDate = strDates[0];
                }
                if (strDates[1].matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                    endDate = strDates[1];
                }
            }
        }

        //
        return this.userService.selectPageList(page, limit, departmentId, userName, realName, mobile, userStatus, startDate, endDate);
    }


    /**
     * 查询部门数据 for 表单下拉树形
     */
    @ReturnJsonMapping("/selectForTreeSelect")
    public List<TreeSelectVo> selectForTreeSelect() {
        return this.userService.selectForTreeSelect();
    }

    /**
     * 查询职位信息 for h5下拉框
     */
    @ReturnJsonMapping("/selectPositionForH5Select")
    public List<H5SelectVo> selectPositionForH5Select() {
        return this.userService.selectPositionForH5Select();
    }

    /**
     * 根据图片名返回图片 流
     */
    @RequestMapping("/getPortraitImage")
    @ResponseBody
    public ResponseEntity<byte[]> getPortraitImage(String imgName) {
        if (Tools.isNotNull(imgName)) {
            //图片名不未空
            String imgPath = UPLOAD_PATH + imgName;
            File fileImg = new File(imgPath);
            if (fileImg.exists()) {
                try {
                    //图片的二进制数组
                    byte[] bs = FileUtils.readFileToByteArray(fileImg);
                    //header  指定返的类型
                    HttpHeaders header = new HttpHeaders();
                    header.add(HttpHeaders.CONTENT_TYPE, Tools.getImageContentType(imgName));
                    return new ResponseEntity<>(bs, header, HttpStatus.OK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * 查询角色信息 for h5下拉框
     */
    @ReturnJsonMapping("/selectRoleForH5Select")
    public List<H5SelectVo> selectRoleForH5Select() {
        return this.userService.selectRoleForH5Select();
    }

    /**
     * 新增
     */
    @ReturnJsonMapping("/insert")
    public JsonMsg insert(SysUser saveUser, MultipartFile portraitFile) {
        JsonMsg jsonMsg = new JsonMsg();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS_");
        //判断文件存放目录是否存在
        File uploadDir = new File(UPLOAD_PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        //处理文件上传
        if (portraitFile != null && !portraitFile.isEmpty()) {
            //拼接文件名  item.getName()--》文件名
            String fileName = dateFormat.format(new Date()) + System.nanoTime() + Tools.getFileExt(portraitFile.getOriginalFilename());
            //存放路径
            String filePath = UPLOAD_PATH + fileName;
            File saveFile = new File(filePath);
            System.err.println(filePath);
            //保存文件到硬盘
            try {
                portraitFile.transferTo(saveFile);
                //把文件名保存到需要新增的对象中
                saveUser.setPortrait(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //用户名
            if (!Tools.isNotNull(saveUser.getUserName())) {
                throw new RuntimeException("请输入用户名！");
            }
            //密码
            if (Tools.isNotNull(saveUser.getUserPassword())) {
                Random random = new Random();
                //生成一个随机的8位数作为盐   10000000 ~ 99999999
                String salt = String.valueOf(random.nextInt(90000000) + 10000000);
                //对输入的密码+盐 取MD5值
                String userPassword = MD5Util.getMD5(saveUser.getUserPassword() + salt);
                saveUser.setUserPassword(userPassword);
                saveUser.setSalt(salt);
            } else {
                throw new RuntimeException("请输入密码！");
            }
            //部门
            if (saveUser.getDepartmentId() == null || saveUser.getDepartmentId() < 1) {
                throw new RuntimeException("请选择部门！");
            }
            //职位
            if (saveUser.getPositionId() == null || saveUser.getPositionId() < 1) {
                throw new RuntimeException("请选择职位！");
            }
            //角色
            if (saveUser.getRoleId() == null || saveUser.getRoleId() < 1) {
                throw new RuntimeException("请选择角色！");
            }
            //状态
            if (saveUser.getUserStatus() == null) {
                throw new RuntimeException("请选择状态！");
            }
            boolean isOk = this.userService.insert(saveUser);
            if (isOk) {
                jsonMsg.setState(true);
                jsonMsg.setMsg("新增成功");
            } else {
                jsonMsg.setMsg("新增失败");
            }
        } catch (RuntimeException e) {
            jsonMsg.setMsg(e.getMessage());
        }
        return jsonMsg;
    }


    /**
     * 查询用户 by id
     */
    @ReturnJsonMapping("/selectById")
    public JsonMsg selectById(Integer id) {
        JsonMsg jsonMsg = new JsonMsg();
        if (id!=null && id>0) {
            SysUser user = this.userService.selectById(id);
            jsonMsg.setState(true);
            jsonMsg.setData(user);
        } else {
            jsonMsg.setMsg("非法访问");
        }
        return jsonMsg;
    }


    /**
     * 修好
     */
    @ReturnJsonMapping("/update")
    public JsonMsg update(SysUser saveUser, MultipartFile portraitFile) {
        JsonMsg jsonMsg = new JsonMsg();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS_");
        //判断文件存放目录是否存在
        File uploadDir = new File(UPLOAD_PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        //处理文件上传
        if (portraitFile != null && !portraitFile.isEmpty()) {
            //拼接文件名  item.getName()--》文件名
            String fileName = dateFormat.format(new Date()) + System.nanoTime() + Tools.getFileExt(portraitFile.getOriginalFilename());
            //存放路径
            String filePath = UPLOAD_PATH + fileName;
            File saveFile = new File(filePath);
            System.err.println(filePath);
            //保存文件到硬盘
            try {
                portraitFile.transferTo(saveFile);
                //把文件名保存到需要新增的对象中
                saveUser.setPortrait(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //用户名
            if (!Tools.isNotNull(saveUser.getUserName())) {
                throw new RuntimeException("请输入用户名！");
            }
            //密码
            if (Tools.isNotNull(saveUser.getUserPassword())) {
                Random random = new Random();
                //生成一个随机的8位数作为盐   10000000 ~ 99999999
                String salt = String.valueOf(random.nextInt(90000000) + 10000000);
                //对输入的密码+盐 取MD5值
                String userPassword = MD5Util.getMD5(saveUser.getUserPassword() + salt);
                saveUser.setUserPassword(userPassword);
                saveUser.setSalt(salt);
            } else {
                throw new RuntimeException("请输入密码！");
            }
            //部门
            if (saveUser.getDepartmentId() == null || saveUser.getDepartmentId() < 1) {
                throw new RuntimeException("请选择部门！");
            }
            //职位
            if (saveUser.getPositionId() == null || saveUser.getPositionId() < 1) {
                throw new RuntimeException("请选择职位！");
            }
            //角色
            if (saveUser.getRoleId() == null || saveUser.getRoleId() < 1) {
                throw new RuntimeException("请选择角色！");
            }
            //状态
            if (saveUser.getUserStatus() == null) {
                throw new RuntimeException("请选择状态！");
            }
            //判断id
            if (saveUser.getId()==null || saveUser.getId()<1){
                throw new RuntimeException("参数异常！");
            }

            //查询未修改的数据
            SysUser oldUser = this.userService.selectById(saveUser.getId());
            //判断是否上传新图片 null未上传  ！=null上传
            boolean isUploadNewImg = saveUser.getPortrait() != null;
            //判断原来是否有图片
            boolean hasOldImg = oldUser.getPortrait() != null;

            boolean isOk = this.userService.update(saveUser);
            if (isOk) {
                //在有新图片上传的情况下  旧图片的删除
                if (isUploadNewImg && hasOldImg) {
                    String oldPath = UPLOAD_PATH + oldUser.getPortrait();
                    File oldImg = new File(oldPath);
                    if (oldImg.exists()) {
                        try {
                            oldImg.delete();
                        } catch (Exception ignored) {

                        }
                    }
                }
                jsonMsg.setState(true);
                jsonMsg.setMsg("修改成功");
            } else {
                //修改失败时 删除新上传的图片
                if (saveUser.getPortrait() != null) {
                    String newPath = UPLOAD_PATH + saveUser.getPortrait();
                    File newImg = new File(newPath);
                    if (newImg.exists()) {
                        try {
                            newImg.delete();
                        } catch (Exception ignored) {

                        }
                    }
                }
                jsonMsg.setMsg("修改失败");
            }
        } catch (RuntimeException e) {
            jsonMsg.setMsg(e.getMessage());
        }
        return jsonMsg;
    }

    /**
     * 删除员工 by id
     */
    @ReturnJsonMapping("/deleteById")
    public JsonMsg deleteById(Integer id) {
        JsonMsg jsonMsg = new JsonMsg();
        if (id!=null && id>0) {
            try {
                boolean isOk = this.userService.deleteById(id);
                if (isOk) {
                    jsonMsg.setState(true);
                    jsonMsg.setMsg("删除成功");
                } else {
                    jsonMsg.setMsg("删除失败");
                }
            }catch (RuntimeException e){
                e.printStackTrace();
                jsonMsg.setMsg("删除失败");
            }
        } else {
            jsonMsg.setMsg("非法访问");
        }
        return jsonMsg;
    }

    /**
     * 重置密码
     */
    @ReturnJsonMapping("/resetPassword")
    public JsonMsg resetPassword(Integer id,String password ) {
        JsonMsg jsonMsg = new JsonMsg();
        if (id!=null && id>0) {
            if (Tools.isNotNull(password)) {
                Random random = new Random();
                //生成一个随机的8位数作为盐   10000000 ~ 99999999
                String salt = String.valueOf(random.nextInt(90000000) + 10000000);
                //对输入的密码+盐 取MD5值
                String userPassword = MD5Util.getMD5(password + salt);
                SysUser user=new SysUser();
                user.setId(id);
                user.setUserPassword(userPassword);
                user.setSalt(salt);
                //调用修改方法
                try {
                    boolean isOk = this.userService.update(user);
                    if (isOk) {
                        jsonMsg.setState(true);
                        jsonMsg.setMsg("重置成功");
                    } else {
                        jsonMsg.setMsg("重置失败");
                    }
                }catch (RuntimeException e){
                    e.printStackTrace();
                    jsonMsg.setMsg("重置失败");
                }
            } else {
                jsonMsg.setMsg("请输入新密码");
            }
        } else {
            jsonMsg.setMsg("非法访问");
        }
        return jsonMsg;
    }


    //导出

    /**
     * 导出为 xlsx
     */
    @RequestMapping("/exportXlsx")
    public void exportXlsx(Integer departmentId,String userName,String realName,
                           String mobile,Integer userStatus,String startEndDate,
                           HttpServletResponse response) throws IOException {
        //departmentId,userName,realName ,mobile ,userStatus ,startEndDate
        //开始时间和结束时间   2021-04-22 - 2021-05-22
        String startDate = null;
        String endDate = null;
        if (Tools.isNotNull(startEndDate)) {
            String[] strDates = startEndDate.split(" - ");
            if (strDates.length == 2) {
                if (strDates[0].matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                    startDate = strDates[0];
                }
                if (strDates[1].matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                    endDate = strDates[1];
                }
            }
        }
        //查询出要导导出的数据
        List<UserVo> exportList = this.userService.selectForExport(departmentId, userName, realName, mobile, userStatus, startDate, endDate);

        //生日时间格式化
        SimpleDateFormat birthdayFormat = new SimpleDateFormat("yyyy-MM-dd");
        //导出时间的格式化
        SimpleDateFormat createFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //导出时间
        Date exportDate = new Date();
        //使用POI导出到excel
        //没有选择用XSSF，而是选择的SXSSF，这样能减少内存消耗，降低了内存溢出的风险。
        //xlsx方式
        //1-创建工作簿 org.apache.poi.xssf.streaming.SXSSFWorkbook
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);//保存在内存中，直到刷新的行数。
        //2-创建工作表对象 org.apache.poi.xssf.streaming.SXSSFSheet
        SXSSFSheet sheet = workbook.createSheet("员工信息");
        //3-写入excel的标题
        //创建第一行为标题行 org.apache.poi.xssf.streaming.SXSSFRow
        SXSSFRow titleRow = sheet.createRow(0);
        // 设置行高使用HSSFRow对象的setHeight和setHeightInPoints方法，
        // 这两个方法的区别在于setHeightInPoints的单位是点，而setHeight的单位是1/20个点
        titleRow.setHeightInPoints(30.0f);
        //合并列
        //创建单元格 org.apache.poi.xssf.streaming.SXSSFCell
        SXSSFCell cell = titleRow.createCell(0);//序号从0开始
        //指定单元格合并范围 org.apache.poi.ss.util.CellRangeAddress
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 14);
        sheet.addMergedRegion(region);//合并单元格
        cell.setCellValue("员工数据  (" + createFormat.format(exportDate) + ")");
        //单元格样式
        //org.apache.poi.ss.usermodel.CellStyle
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中 org.apache.poi.ss.usermodel.HorizontalAlignment
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中 org.apache.poi.ss.usermodel.VerticalAlignment
        Font titleFont = workbook.createFont();//org.apache.poi.ss.usermodel.Font
        titleFont.setFontHeightInPoints((short) 24);//字体大小
        titleStyle.setFont(titleFont);//字体
        titleStyle.setBorderLeft(BorderStyle.THIN);//边框 左 org.apache.poi.ss.usermodel.BorderStyle
        titleStyle.setBorderTop(BorderStyle.THIN);//边框 上
        titleStyle.setBorderRight(BorderStyle.THIN);//边框 右
        titleStyle.setBorderBottom(BorderStyle.THIN);//边框 下
        //把样式设置给单元格
        titleRow.getCell(0).setCellStyle(titleStyle);

        //4-表头
        SXSSFRow headerNameRow = sheet.createRow(1);
        headerNameRow.createCell(0).setCellValue("序号");
        headerNameRow.createCell(1).setCellValue("登录名");
        headerNameRow.createCell(2).setCellValue("姓名");
        headerNameRow.createCell(3).setCellValue("部门");
        headerNameRow.createCell(4).setCellValue("职位");
        headerNameRow.createCell(5).setCellValue("角色");
        headerNameRow.createCell(6).setCellValue("性别");
        headerNameRow.createCell(7).setCellValue("出生日期");
        headerNameRow.createCell(8).setCellValue("Email");
        headerNameRow.createCell(9).setCellValue("手机号");
        headerNameRow.createCell(10).setCellValue("QQ");
        headerNameRow.createCell(11).setCellValue("微信");
        headerNameRow.createCell(12).setCellValue("状态");
        headerNameRow.createCell(13).setCellValue("创建时间");
        headerNameRow.createCell(14).setCellValue("备注");
        //样式
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setBorderLeft(BorderStyle.THIN);//边框 左
        headerStyle.setBorderTop(BorderStyle.THIN);//边框 上
        headerStyle.setBorderRight(BorderStyle.THIN);//边框 右
        headerStyle.setBorderBottom(BorderStyle.THIN);//边框 下
        //背景颜色  org.apache.poi.ss.usermodel.IndexedColors
        headerStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());//背景颜色
        //org.apache.poi.ss.usermodel.FillPatternType
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);//加粗
        headerStyle.setFont(headerFont);
        //通过循环给表头设置样式 getPhysicalNumberOfCells()获取单元格个数
        for (int i = 0; i < headerNameRow.getPhysicalNumberOfCells(); i++) {
            headerNameRow.getCell(i).setCellStyle(headerStyle);
        }

        //5-写数据
        if (exportList != null && exportList.size() > 0) {
            //数据部分 样式
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setBorderLeft(BorderStyle.THIN);//边框 左
            dateStyle.setBorderTop(BorderStyle.THIN);//边框 上
            dateStyle.setBorderRight(BorderStyle.THIN);//边框 右
            dateStyle.setBorderBottom(BorderStyle.THIN);//边框 下
            dateStyle.setAlignment(HorizontalAlignment.LEFT);//左对齐

            for (int i = 0; i < exportList.size(); i++) {
                UserVo userVo = exportList.get(i);
                //第一行标题，第二行是表头
                Row rowNow = sheet.createRow(2 + i);
                //设置值
                rowNow.createCell(0).setCellValue(i + 1);//序号
                rowNow.createCell(1).setCellValue(userVo.getUserName());//登录名
                rowNow.createCell(2).setCellValue(userVo.getRealName());//姓名
                rowNow.createCell(3).setCellValue(userVo.getDepartmentName());//部门
                rowNow.createCell(4).setCellValue(userVo.getPositionName());//职位
                rowNow.createCell(5).setCellValue(userVo.getRoleName());//角色
                String strGender = "未知";
                switch (userVo.getGender()) {
                    case 1:
                        strGender = "男";
                        break;
                    case 2:
                        strGender = "女";
                        break;
                }
                rowNow.createCell(6).setCellValue(strGender);//性别
                if (userVo.getBirthday() != null) {
                    rowNow.createCell(7).setCellValue(birthdayFormat.format(userVo.getBirthday()));//出生日期
                }
                rowNow.createCell(8).setCellValue(userVo.getEmail());//Email
                rowNow.createCell(9).setCellValue(userVo.getMobile());//手机号
                rowNow.createCell(10).setCellValue(userVo.getQq());//QQ
                rowNow.createCell(11).setCellValue(userVo.getWechat());//微信
                rowNow.createCell(12).setCellValue(userVo.getUserStatus() == 0 ? "禁用" : "启用");//状态
                if (userVo.getGmtCreate() != null) {
                    rowNow.createCell(13).setCellValue(createFormat.format(userVo.getGmtCreate()));//创建时间
                }
                rowNow.createCell(14).setCellValue(userVo.getRemark());//备注

                //设置样式
                for (int j = 0; j < rowNow.getPhysicalNumberOfCells(); j++) {
                    rowNow.getCell(j).setCellStyle(dateStyle);
                }
            }

            //设置自动列宽 如果使了SXSSF，需要把所有数据加载到内存，数据比较多时容易内存条溢出，注意！！！！！
            sheet.trackAllColumnsForAutoSizing();//缓存所有数据 for 自动宽度
            for (int i = 0; i < headerNameRow.getPhysicalNumberOfCells(); i++) {
                sheet.autoSizeColumn(i);//设置自动列宽
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 17 / 10);
            }

            //6-输出
            SimpleDateFormat fileFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String lastFileName = "员工数据" + fileFormat.format(new Date()) + ".xlsx";//文件名
            response.setContentType("application/msexcel;charset=UTF-8");//指定xlsx的MIME类型
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(lastFileName, "UTF-8"));//指定文件下载名称
            //获取输出流
            OutputStream out = response.getOutputStream();
            workbook.write(out);//把工作簿写到输出流
            out.flush();
            out.close();
        }

    }

    @RequestMapping("/exportXls")
    @ResponseBody
    public ResponseEntity<byte[]> exportXls(Integer departmentId,String userName,String realName,
                          String mobile,Integer userStatus,String startEndDate) throws IOException {
        //departmentId,userName,realName ,mobile ,userStatus ,startEndDate
        //开始时间和结束时间   2021-04-22 - 2021-05-22
        String startDate = null;
        String endDate = null;
        if (Tools.isNotNull(startEndDate)) {
            String[] strDates = startEndDate.split(" - ");
            if (strDates.length == 2) {
                if (strDates[0].matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                    startDate = strDates[0];
                }
                if (strDates[1].matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                    endDate = strDates[1];
                }
            }
        }
        List<UserVo> exportList = this.userService.selectForExport(departmentId, userName, realName, mobile, userStatus, startDate, endDate);


        SimpleDateFormat birthdayFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat createFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date exportDate = new Date();
        //使用POI导出到excel
        //xls方式 org.apache.poi.hssf.usermodel.HSSF
        //1-创建工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();//保存在内存中，直到刷新的行数。
        //2-创建工作表对象 SXSSFSheet
        HSSFSheet sheet = workbook.createSheet("员工信息");
        //3-写入excel的标题
        //创建第一行为标题行
        HSSFRow titleRow = sheet.createRow(0);
        // 设置行高使用HSSFRow对象的setHeight和setHeightInPoints方法，这两个方法的区别在于setHeightInPoints的单位是点，而setHeight的单位是1/20个点
        titleRow.setHeightInPoints(30.0f);
        //合并列
        HSSFCell cell = titleRow.createCell(0);
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 14);
        sheet.addMergedRegion(region);
        cell.setCellValue("员工数据  (" + createFormat.format(exportDate) + ")");
        //单元格样式
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 24);//字体大小
        titleStyle.setFont(titleFont);//字体
        titleStyle.setBorderLeft(BorderStyle.THIN);//边框 左
        titleStyle.setBorderTop(BorderStyle.THIN);//边框 上
        titleStyle.setBorderRight(BorderStyle.THIN);//边框 右
        titleStyle.setBorderBottom(BorderStyle.THIN);//边框 下
        titleRow.getCell(0).setCellStyle(titleStyle);


        //4-表头
        HSSFRow headerNameRow = sheet.createRow(1);
        headerNameRow.createCell(0).setCellValue("序号");
        headerNameRow.createCell(1).setCellValue("登录名");
        headerNameRow.createCell(2).setCellValue("姓名");
        headerNameRow.createCell(3).setCellValue("部门");
        headerNameRow.createCell(4).setCellValue("职位");
        headerNameRow.createCell(5).setCellValue("角色");
        headerNameRow.createCell(6).setCellValue("性别");
        headerNameRow.createCell(7).setCellValue("出生日期");
        headerNameRow.createCell(8).setCellValue("Email");
        headerNameRow.createCell(9).setCellValue("手机号");
        headerNameRow.createCell(10).setCellValue("QQ");
        headerNameRow.createCell(11).setCellValue("微信");
        headerNameRow.createCell(12).setCellValue("状态");
        headerNameRow.createCell(13).setCellValue("创建时间");
        headerNameRow.createCell(14).setCellValue("备注");
        //样式
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setBorderLeft(BorderStyle.THIN);//边框 左
        headerStyle.setBorderTop(BorderStyle.THIN);//边框 上
        headerStyle.setBorderRight(BorderStyle.THIN);//边框 右
        headerStyle.setBorderBottom(BorderStyle.THIN);//边框 下
        headerStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());//背景颜色
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);//加粗
        headerStyle.setFont(headerFont);
        for (int i = 0; i < headerNameRow.getPhysicalNumberOfCells(); i++) {
            headerNameRow.getCell(i).setCellStyle(headerStyle);
        }

        //5-写数据
        if (exportList != null && exportList.size() > 0) {
            //数据部分 样式
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setBorderLeft(BorderStyle.THIN);//边框 左
            dateStyle.setBorderTop(BorderStyle.THIN);//边框 上
            dateStyle.setBorderRight(BorderStyle.THIN);//边框 右
            dateStyle.setBorderBottom(BorderStyle.THIN);//边框 下
            dateStyle.setAlignment(HorizontalAlignment.LEFT);//左对齐

            for (int i = 0; i < exportList.size(); i++) {
                UserVo userVo = exportList.get(i);
                //第一行标题，第二行是表头
                Row rowNow = sheet.createRow(2 + i);
                //设置值
                rowNow.createCell(0).setCellValue(i + 1);//序号
                rowNow.createCell(1).setCellValue(userVo.getUserName());//登录名
                rowNow.createCell(2).setCellValue(userVo.getRealName());//姓名
                rowNow.createCell(3).setCellValue(userVo.getDepartmentName());//部门
                rowNow.createCell(4).setCellValue(userVo.getPositionName());//职位
                rowNow.createCell(5).setCellValue(userVo.getRoleName());//角色
                String strGender = "未知";
                switch (userVo.getGender()) {
                    case 1:
                        strGender = "男";
                        break;
                    case 2:
                        strGender = "女";
                        break;
                }
                rowNow.createCell(6).setCellValue(strGender);//性别
                if (userVo.getBirthday() != null) {
                    rowNow.createCell(7).setCellValue(birthdayFormat.format(userVo.getBirthday()));//出生日期
                }
                rowNow.createCell(8).setCellValue(userVo.getEmail());//Email
                rowNow.createCell(9).setCellValue(userVo.getMobile());//手机号
                rowNow.createCell(10).setCellValue(userVo.getQq());//QQ
                rowNow.createCell(11).setCellValue(userVo.getWechat());//微信
                rowNow.createCell(12).setCellValue(userVo.getUserStatus() == 0 ? "禁用" : "启用");//状态
                if (userVo.getGmtCreate() != null) {
                    rowNow.createCell(13).setCellValue(createFormat.format(userVo.getGmtCreate()));//创建时间
                }
                rowNow.createCell(14).setCellValue(userVo.getRemark());//备注

                //设置样式
                for (int j = 0; j < rowNow.getPhysicalNumberOfCells(); j++) {
                    rowNow.getCell(j).setCellStyle(dateStyle);
                }
            }

            //设置自动列宽
            for (int i = 0; i < headerNameRow.getPhysicalNumberOfCells(); i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 17 / 10);
            }

            //6-输出
            SimpleDateFormat fileFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String lastFileName = "员工数据" + fileFormat.format(new Date()) + ".xls";//文件名

            //通过ResponseEntity返回
            HttpHeaders headers=new HttpHeaders();
//application/octet-stream二进制流数据（最常见的文件下载）
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//通知浏览器以attachment（下载方式） 下载文件，文件名称为指定名称
            lastFileName=URLEncoder.encode(lastFileName,"UTF-8");
            headers.setContentDispositionFormData("attachment", lastFileName);
//获取workbook的二进制
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            workbook.write(baos);//把workbook输出到 ByteArrayOutputStream
            byte[] bytes=baos.toByteArray();//从ByteArrayOutputStream中获取 byte[]
//返回文件
            return new ResponseEntity<byte[]>(bytes,headers, HttpStatus.CREATED);
        }
        return null;
    }

    /**
     * Excel文件上传 导入用户
     * 前面部分就是文件上传
     * 中间就是 读取Excel的数据 并匹配判断
     * 后面就是调用Service层，返回
     */
    @ReturnJsonMapping("/uploadExcel")
    public JsonMsg uploadExcel(MultipartFile excelFile) throws IOException {
        JsonMsg jsonMsg = new JsonMsg();
        // 解析请求的内容 获取上传文件的IO流
        InputStream inFile = null;//上传的文件的输入流
        String excelExt = "";//上传文件的扩展名 .xls .xlsx
        if (excelFile!=null && !excelFile.isEmpty()){
            inFile=excelFile.getInputStream();//从上传的文件中获取输入流
            excelExt=Tools.getFileExt(excelFile.getOriginalFilename());
        }

        //解析Xls xlsx文件
        if (inFile != null) {
            //1、 定义工作簿
            Workbook workbook = null;
            //2、判断后缀.决定使用的解析方式.
            if (".xls".equals(excelExt)) {
                // 2003
                workbook = new HSSFWorkbook(inFile);
            } else if (".xlsx".equals(excelExt)) {
                // 2007
                workbook = new XSSFWorkbook(inFile);
            } else {
                // 未知内容
                jsonMsg.setMsg("请上传Excel文件(xls、xlsx)");
                return jsonMsg;
            }

            // 3、获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                jsonMsg.setMsg("上传Excel文件中不存在工作表，请检查");
                return jsonMsg;
            }
            // 4、判断是否有数据
            // 获取表格中最后一行的行号
            int lastRowNum = sheet.getLastRowNum();
            if (lastRowNum < 2) {//0行是说明，1行是标题
                jsonMsg.setMsg("请录入数据");
                return jsonMsg;
            }
            // 5、循环读取数据
            List<SysUser> saveUsers = new ArrayList<>();//存放导入的用户数据
            List<String> strErrorInfo = new ArrayList<>();//存放转换过程中的错误信息
            //查询出所有的用户信息，用于比较用户名是否存在（根据项目实际情况，如果用户较少，这种方式更快，
            // 如果用户太多（几万级以上）直接通过name去数据库查询更快）
            List<SysUser> dbSysUsers = this.userService.selectAll();
            List<H5SelectVo> dbSysPosition = this.userService.selectPositionForH5Select();//所有的职务表，用于匹配
            List<H5SelectVo> dbSysRole = userService.selectRoleForH5Select();//所有的角色 用于匹配
            List<TreeSelectVo> dbDepartment = this.userService.selectForTreeSelect();//所有的部门，等级存放，方便查找
            //出生日期格式化
            SimpleDateFormat birthdayFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 2; i <= lastRowNum; i++) {
                //读取行
                Row thisRow = sheet.getRow(i);
                //读取列
                String strIndex = thisRow.getCell(0) == null ? "" : thisRow.getCell(0).toString();
                String userName = thisRow.getCell(1) == null ? "" : thisRow.getCell(1).toString();//用户名
                String userPassword = thisRow.getCell(2) == null ? "" : thisRow.getCell(2).toString();//密目
                String realName = thisRow.getCell(3) == null ? "" : thisRow.getCell(3).toString();//真实姓名
                String strDepartment = thisRow.getCell(4) == null ? "" : thisRow.getCell(4).toString();//部门
                String strPosition = thisRow.getCell(5) == null ? "" : thisRow.getCell(5).toString();//职位
                String strRole = thisRow.getCell(6) == null ? "" : thisRow.getCell(6).toString();//角色
                String strGender = thisRow.getCell(7) == null ? "" : thisRow.getCell(7).toString();//性别
                String strBirthday = thisRow.getCell(8) == null ? "" : thisRow.getCell(8).toString();//出生日期
                String strEmail = thisRow.getCell(9) == null ? "" : thisRow.getCell(9).toString();//Email
                String strMobile = thisRow.getCell(10) == null ? "" : thisRow.getCell(10).toString();//手机号
                String strQQ = thisRow.getCell(11) == null ? "" : thisRow.getCell(11).toString();//QQ
                String strWechat = thisRow.getCell(12) == null ? "" : thisRow.getCell(12).toString();//微信
                String strUserStatus = thisRow.getCell(13) == null ? "" : thisRow.getCell(13).toString();//状态
                String strRemark = thisRow.getCell(14) == null ? "" : thisRow.getCell(14).toString();//备注
                //数据转换 匹配 验证
                boolean isError = false;
                SysUser saveUser = new SysUser();
                //====用户名
                userName = userName.trim();
                //查找是否存在 数据库中
                for (SysUser item : dbSysUsers) {
                    if (item.getUserName().trim().equals(userName)) {
                        //存在，记录错误
                        strErrorInfo.add("第" + strIndex + "条数据：用户名" + userName + "已经存在于系统中");
                        isError = true;
                        break;
                    }
                }
                if (isError) continue;
                //查找是否存在 之前的用户中
                for (SysUser item : saveUsers) {
                    if (item.getUserName().trim().equals(userName)) {
                        //存在，记录错误
                        strErrorInfo.add("第" + strIndex + "条数据：用户名" + userName + "已经存在于本次导入的用户中");
                        isError = true;
                        break;
                    }
                }
                if (isError) continue;
                saveUser.setUserName(userName);
                //===密码
                if (Tools.isNotNull(userPassword) && userName.length() >= 6) {
                    Random random = new Random();
                    //生成一个随机的8位数作为盐
                    String salt = String.valueOf(random.nextInt(90000000) + 10000000);
                    //对输入的密码+盐 取MD5值
                    userPassword = MD5Util.getMD5(userPassword + salt);
                    saveUser.setUserPassword(userPassword);
                    saveUser.setSalt(salt);
                } else {
                    strErrorInfo.add("第" + strIndex + "条数据【" + userName + "】未输入密码或密码不足6位");
                    continue;
                }
                //==真实姓名
                if (Tools.isNotNull(realName)) {
                    saveUser.setRealName(realName.trim());
                } else {
                    strErrorInfo.add("第" + strIndex + "条数据【" + userName + "】未输入真实姓名");
                    continue;
                }
                //==部门 部门按照“集团-广州总公司-研发部”的格式填写
                if (Tools.isNotNull(strDepartment)) {
                    String[] strDepartments = strDepartment.split("-");
                    List<String> listDepartments = Arrays.asList(strDepartments);//字符串 数组转 list
                    //Arrays.asList()生产的List的add、remove方法时报异常，这是由Arrays.asList() 返回的市Arrays的内部类ArrayList， 而不是java.util.ArrayList
                    listDepartments = new ArrayList<>(listDepartments);
                    if (listDepartments.size() > 0) {
                        //调用的是树形下拉的数据，最外层是“根”
                        int departmentId = findDepartmentByName(dbDepartment, listDepartments);
                        if (departmentId > 0) {
                            saveUser.setDepartmentId(departmentId);
                        } else {
                            strErrorInfo.add("第" + strIndex + "条数据【" + userName + "】未匹配到输入的部门名称");
                            continue;
                        }
                    } else {
                        strErrorInfo.add("第" + strIndex + "条数据【" + userName + "】部门输入格式有误");
                        continue;
                    }
                } else {
                    strErrorInfo.add("第" + strIndex + "条数据【" + userName + "】未输入 部门信息");
                    continue;
                }
                //===职位
                if (Tools.isNotNull(strPosition)) {
                    int positionId = 0;
                    for (H5SelectVo position : dbSysPosition) {
                        if (position.getText().trim().equals(strPosition.trim())) {
                            positionId = Integer.parseInt(position.getValue());
                            break;
                        }
                    }
                    if (positionId > 0) {
                        saveUser.setPositionId(positionId);
                    } else {
                        strErrorInfo.add("第" + strIndex + "条数据【" + userName + "】未匹配到输入的职位名称");
                        continue;
                    }
                } else {
                    strErrorInfo.add("第" + strIndex + "条数据【" + userName + "】未输入 职位信息");
                    continue;
                }
                //====角色
                if (Tools.isNotNull(strRole)) {
                    int roleId = 0;
                    for (H5SelectVo role : dbSysRole) {
                        if (role.getText().trim().equals(strRole.trim())) {
                            roleId = Integer.parseInt(role.getValue());
                            break;
                        }
                    }
                    if (roleId > 0) {
                        saveUser.setRoleId(roleId);
                    } else {
                        strErrorInfo.add("第" + strIndex + "条数据【" + userName + "】未匹配到输入的角色名称");
                        continue;
                    }
                } else {
                    strErrorInfo.add("第" + strIndex + "条数据【" + userName + "】未输入 角色信息");
                    continue;
                }
                //===性别
                if (Tools.isNotNull(strGender)) {
                    if ("男".equals(strGender.trim())) {
                        saveUser.setGender((byte) 1);//男
                    } else if ("女".equals(strGender.trim())) {
                        saveUser.setGender((byte) 2);//女
                    } else {
                        saveUser.setGender((byte) 0);//未知
                    }
                } else {
                    saveUser.setGender((byte) 0);//未知
                }
                //===出生日期
                if (Tools.isNotNull(strBirthday)) {
                    try {
                        Date birthday = birthdayFormat.parse(strBirthday.trim());
                        saveUser.setBirthday(birthday);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        strErrorInfo.add("第" + strIndex + "条数据【" + userName + "】输入的出生日期格式有误");
                        continue;
                    }
                }
                //====Email 未做格式校验
                if (Tools.isNotNull(strEmail)) {
                    saveUser.setEmail(strEmail.trim());
                }
                //===手机号
                if (Tools.isNotNull(strMobile) && strMobile.trim().length() == 11) {
                    saveUser.setMobile(strMobile);
                } else {
                    strErrorInfo.add("第" + strIndex + "条数据【" + userName + "】未输入手机号或手机号格式不正确");
                    continue;
                }
                //===QQ
                if (Tools.isNotNull(strQQ)) {
                    saveUser.setQq(strQQ.trim());
                }
                //===微信
                if (Tools.isNotNull(strWechat)) {
                    saveUser.setWechat(strWechat.trim());
                }
                //===状态(0禁用 1启用)
                if (Tools.isNotNull(strUserStatus)) {
                    if ("启用".equals(strUserStatus.trim())) {
                        saveUser.setUserStatus((byte) 1);
                    } else if ("禁用".equals(strUserStatus.trim())) {
                        saveUser.setUserStatus((byte) 0);
                    } else {
                        strErrorInfo.add("第" + strIndex + "条数据【" + userName + "】输入的用户状态不正确");
                        continue;
                    }
                } else {
                    strErrorInfo.add("第" + strIndex + "条数据【" + userName + "】未输入用户状态");
                    continue;
                }
                //===备注
                if (Tools.isNotNull(strRemark)) {
                    saveUser.setRemark(strRemark.trim());
                }
                //添加到集合中
                saveUsers.add(saveUser);
            }
            //判断是否出现错误
            if (strErrorInfo.size() > 0) {
                //有错误
                String msg = String.join("<br>", strErrorInfo);
                jsonMsg.setMsg("数据匹配出错：<br>" + msg);
            } else {
                //无错误 调用保存
                if (this.userService.saveUpload(saveUsers)) {
                    //成功
                    jsonMsg.setState(true);
                    jsonMsg.setMsg("导入成功");
                } else {
                    jsonMsg.setMsg("导入失败：保存数据时发生错误，为了您的数据安全，数据已经回退到导入前！");
                }
            }
        } else {
            jsonMsg.setMsg("请上传文件");
        }

        return jsonMsg;
    }


    private int findDepartmentByName(List<TreeSelectVo> dbDepartment, List<String> listDepartments) {
        String strDepartmentName = listDepartments.get(0);//获取名称中第一个
        for (TreeSelectVo itme : dbDepartment) {
            if (itme.getName().trim().equals(strDepartmentName.trim())) {
                //查询到
                listDepartments.remove(0);//查找到的第一name移除
                //判断是否还有需要查找的下一级 部门名称
                if (listDepartments.size() > 0) {
                    //有继续在子部门中查询下一个名称
                    return findDepartmentByName(itme.getChildren(), listDepartments);
                } else {
                    //没有了 直接返回查找到的id
                    return itme.getId();
                }
            } else {
                //未找到
                return 0;
            }
        }
        return 0;
    }
}
