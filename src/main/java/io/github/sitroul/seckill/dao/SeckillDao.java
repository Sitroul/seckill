package io.github.sitroul.seckill.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.sitroul.seckill.entity.Seckill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SeckillDao extends BaseMapper<Seckill> {
    @Update("UPDATE seckill SET number = number - 1 WHERE seckill_id = #{seckillId} AND number > 0")
    int reduceStock(Long seckillId);
}
