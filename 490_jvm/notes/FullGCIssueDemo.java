import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 从数据库中读取信用数据，套用模型，并把结果进行记录和传输
 */

public class FullGCIssueDemo {

	// 模拟一张信用卡
    private static class CardInfo {
    	// 信用卡信息
        BigDecimal price = new BigDecimal(0.0);
        String name = "张三";
        int age = 5;
        Date birthdate = new Date();
        // 模拟业务处理方法
        public void m() {
        }
    }

    // 模拟取数据库操作，从数据库中取100个信用卡信息
    private static List<CardInfo> getAllCardInfo(){
        List<CardInfo> taskList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            CardInfo ci = new CardInfo();
            taskList.add(ci);
        }

        return taskList;
    }

    // 模拟风控方法
    private static void modelFit(){
    	// 取100张信用卡的信息
        List<CardInfo> taskList = getAllCardInfo();
        
        // 调用与这些的业务代码
        taskList.forEach(info -> {
            executor.scheduleWithFixedDelay(() -> {
                info.m();
            }, 2 /*第一次延后2毫秒*/, 3 /*每3秒钟执行一次*/, TimeUnit.SECONDS);
        });
    }

    // 50个线程组成的线程池，每个线程都不断执行风控方法（每次调用之间间隔100毫秒
    private static ScheduledThreadPoolExecutor executor
    	 = new ScheduledThreadPoolExecutor(
    	 	50, new ThreadPoolExecutor. DiscardOldestPolicy());

    public static void main(String[] args) throws Exception {
        executor.setMaximumPoolSize(50);

        for (;;){
            modelFit();
            Thread.sleep(100);
        }
    }
}
