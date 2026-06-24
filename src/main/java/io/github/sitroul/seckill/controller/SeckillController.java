package io.github.sitroul.seckill.controller;

import io.github.sitroul.seckill.dto.SeckillResult;
import io.github.sitroul.seckill.service.SeckillService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seckill")
@AllArgsConstructor
public class SeckillController {

    private final SeckillService seckillService;

    @PostMapping("/{seckillId}/execution")
    public SeckillResult<Void> execute(@PathVariable Long seckillId,
                                       @RequestParam Long userId) {
        if (seckillId == null || seckillId <= 0) {
            return SeckillResult.fail("参数错误");
        }
        if (userId == null || userId <= 0) {
            return SeckillResult.fail("参数错误");
        }
        return seckillService.execute(seckillId, userId);
    }
}
