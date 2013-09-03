package org.toughradius.data;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.toughradius.model.RadOnline;
import org.toughradius.model.RadOnlineExample;
import org.toughradius.model.RadOnlineKey;

public interface RadOnlineMapper {
    int countByExample(RadOnlineExample example);

    int deleteByExample(RadOnlineExample example);

    int deleteByPrimaryKey(RadOnlineKey key);

    int insert(RadOnline record);

    int insertSelective(RadOnline record);

    List<RadOnline> selectByExampleWithRowbounds(RadOnlineExample example, RowBounds rowBounds);

    List<RadOnline> selectByExample(RadOnlineExample example);

    RadOnline selectByPrimaryKey(RadOnlineKey key);

    int updateByExampleSelective(@Param("record") RadOnline record, @Param("example") RadOnlineExample example);

    int updateByExample(@Param("record") RadOnline record, @Param("example") RadOnlineExample example);

    int updateByPrimaryKeySelective(RadOnline record);

    int updateByPrimaryKey(RadOnline record);
}