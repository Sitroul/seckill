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
        return seckillService.execute(seckillId, userId);
    }
}