package com.beinglee.top.customizelimitLatch;

import com.beinglee.top.customize.Application;
import com.beinglee.top.customize.LimitLatch;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SpringBootTest(classes = Application.class)
class CustomizeLimitLatchApplicationTests {

    @Test
    void contextLoads() {
    }

    Executor executor = Executors.newFixedThreadPool(10);

    @Test
    public void limitLatchTest() {
        LimitLatch latch = new LimitLatch(2);
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executor.execute(() -> {
                try {
                    latch.countUpOrWait();
                    System.out.println("This is The limitLatchTest!" + finalI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countdown();
                }
            });
        }

    }

}
