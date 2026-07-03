package io.github.sitroul.seckill.service;

import io.github.sitroul.seckill.dao.OrderDao;
import io.github.sitroul.seckill.entity.SeckillOrder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderDao orderDao;

    public void save(SeckillOrder order) {
        orderDao.insert(order);
    }
}