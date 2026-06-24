package io.github.sitroul.seckill.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

// 订单
@Data
public class SeckillOrder {
    @TableId
    private Long orderId;
    private Long seckillId;
    private Long userId;
    private Short state;           // -1无效 0成功 1已付款
    private LocalDateTime createTime;
}