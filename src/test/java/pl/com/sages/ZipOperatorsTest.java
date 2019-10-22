package pl.com.sages;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.function.BiFunction;

@Slf4j
public class ZipOperatorsTest {

    private Flux<Integer> flux1WithDelays() {
        return Flux.create(sink -> {
            log.info("1");
            try {
                sink.next(0);
                sink.next(1);
                Thread.sleep(200);
                sink.next(2);
                sink.next(3);
                Thread.sleep(400);
                sink.next(4);
                sink.next(5);
                Thread.sleep(100);
                sink.next(6);
                sink.next(7);
                Thread.sleep(300);
                sink.complete();
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }

    private Flux<Integer> flux2WithDelays() {
        return Flux.create(sink -> {
            log.info("2");
            try {
                sink.next(10);
                Thread.sleep(300);
                sink.next(20);
                Thread.sleep(200);
                sink.next(30);
                Thread.sleep(400);
                sink.next(40);
                Thread.sleep(100);
                sink.next(50);
                sink.complete();
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }

    @Test
    public void zipTest() {
        Flux<Integer> flux = flux1WithDelays().zipWith(flux2WithDelays(), (BiFunction<Integer, Integer, Integer>) Integer::sum);
        flux.timestamp().subscribe(e -> log.info("Timestamp {} ms: {}", Instant.ofEpochMilli(e.getT1()), e.getT2()));
    }

    @Test
    public void combineLatestTest() throws Exception {
        Flux<Integer> flux = Flux.combineLatest(flux1WithDelays().subscribeOn(Schedulers.elastic()), flux2WithDelays().subscribeOn(Schedulers.elastic()), (BiFunction<Integer, Integer, Integer>) Integer::sum);
        flux.timestamp().subscribe(e -> log.info("Timestamp {} ms: {}", Instant.ofEpochMilli(e.getT1()), e.getT2()));
        Thread.sleep(5000);

    }

}
