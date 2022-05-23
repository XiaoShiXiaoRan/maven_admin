package com.gx.service.impl;

import com.gx.dao.ISysPositionDao;
import com.gx.dao.ISysUserDao;
import com.gx.exception.MyDataException;
import com.gx.po.SysPosition;
import com.gx.service.IPositionService;
import com.gx.vo.JsonMsg;
import com.gx.vo.LayuiTableData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Service("positionService")
@Transactional
public class PositionServiceImpl implements IPositionService {
    //dao
    @Autowired
    private ISysPositionDao positionDao;
    @Autowired
    private ISysUserDao userDao;

    @Override
    @Transactional(readOnly = true)
    public LayuiTableData<SysPosition> selectForPageList(int page, int limit, String searchName, Integer status) {
        //=调用dao查询数据
        List<SysPosition> list=this.positionDao.selectForPageList(page,limit,searchName,status);
        int count=this.positionDao.countAll(searchName,status);
        //组装数据
        return new LayuiTableData<>(count,list);
    }

    @Override
    @Transactional(readOnly = true)
    public int countAll() {
        return this.positionDao.countAll(null,null);
    }

    @Override
    public SysPosition selectById(int id) {
        return this.positionDao.selectById(id);
    }

    @Override
    public boolean insert(SysPosition position) {
        //查询理论下一序号
        int nextSort=this.countAll()+1;
        //传入的序号 小于 nextSort，用户调小的自动生成的序号
        if (nextSort> position.getPositionSort()){
            int r= this.positionDao.updateSortPlus1(position.getPositionSort(),null);
            System.out.println("r="+r);
        }
        //新增
        boolean isOk=this.positionDao.insert(position)>0;
        if (isOk){
            throw new MyDataException("新增操作失败: "+position);
        }
        return true;
    }

    @Override
    public boolean update(SysPosition position) {
        // return this.positionDao.update(position);
        //查询出未修改的数据
        SysPosition dbPosition=this.positionDao.selectById(position.getId());
        if (dbPosition.getPositionSort()>position.getPositionSort()){
            //序号向前（小）移动    大于等于新的序号，小于旧的序号 +1
            this.positionDao.updateSortPlus1(position.getPositionSort(), dbPosition.getPositionSort()-1);
        }else{
            //序号向后（大）移动    大于旧的序号，小于等于新的序号  -1
            this.positionDao.updateSortMinus1(dbPosition.getPositionSort()+1, position.getPositionSort());
        }
        //修改
        boolean isOk=this.positionDao.update(position)>0;
        if (!isOk){
            throw new RuntimeException("修改操作失败: "+position);
        }
        return true;
    }


    @Override
    public JsonMsg deleteById(int id) {
        JsonMsg msg=new JsonMsg();
        //查询准备删除的职位是否在使用
        int useCount=this.userDao.countUserByPositionId(id);
        if (useCount==0){
            SysPosition dbPosition=this.positionDao.selectById(id);
            //更新序号
            this.positionDao.updateSortMinus1(dbPosition.getPositionSort()+1,null);
            //执行删除
            boolean isOk=this.positionDao.deleteById(id)>0;
            if (isOk){
                msg.setState(true);
                msg.setMsg("删除成功");
                //事务提交
            }else {
                throw new RuntimeException("删除操作失败："+id);
            }
        }else {
            //使用中
            msg.setState(false);
            msg.setMsg("该职位使用中，不能删除");
        }
        return msg;
    }
}
