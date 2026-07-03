package io.github.sitroul.seckill.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.sitroul.seckill.entity.Seckill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SeckillDao extends BaseMapper<Seckill> {

    @Select("SELECT seckill_id, name, number, start_time, end_time, create_time FROM seckill")
    List<Seckill> selectAll();

    @Update("UPDATE seckill SET number = number - 1 WHERE seckill_id = #{seckillId} AND number > 0")
    int reduceStock(@Param("seckillId") Long seckillId);
}