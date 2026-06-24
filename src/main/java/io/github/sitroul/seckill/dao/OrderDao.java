package io.github.sitroul.seckill.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.core.annotation.Order;

@Mapper
public interface OrderDao extends BaseMapper<Order> {
    // 减库存
    @Update("UPDATE seckill SET number = number - 1 WHERE seckill_id = #{seckillId} AND number > 0")
    int reduceStock(@Param("seckillId") Long seckillId);

    // 查库存
    @Select("SELECT number FROM seckill WHERE seckill_id = #{seckillId}")
    Integer getStock(@Param("seckillId") Long seckillId);
}
