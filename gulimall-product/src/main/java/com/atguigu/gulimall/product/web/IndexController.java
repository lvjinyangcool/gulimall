package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @RequestMapping({"/", "index", "/index.html"})
    public String indexPage(Model model) {
        // 获取一级分类所有缓存
        List<CategoryEntity> categorys = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", categorys);
        return "index";
    }

    @ResponseBody
    @RequestMapping("index/json/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson() {

        Map<String, List<Catelog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }


    /**
     * RLock锁有看门狗机制 会自动帮我们续期，默认三十秒自动过期
     * lock.lock(10,TimeUnit.SECONDS); 自动解锁的时间一定要大于业务的时间 否则会出现死锁的情况
     * <p>
     * 如果我们传递了锁的超时时间就给redis发送执行脚本 默认超时时间就是我们指定的
     * 如果我们未指定，就使用 30 * 1000 [LockWatchdogTimeout]
     * 只要占锁成功 就会启动一个定时任务 任务就是重新给锁设置过期时间 这个时间还是 [LockWatchdogTimeout] 的时间 1/3 看门狗的时间续期一次 续成满时间
     */
    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        //1丶获取一把锁，只要锁的名字一样，就是通一把锁
        RLock lock = redissonClient.getLock("my-lock");

        //2.加锁
        lock.lock(); //阻塞式等待 默认加的锁都是30s时间
        //1)丶锁的自动续期，如果业务超长，运行期间自动给锁续上新的30s
        //2)丶加锁的业务只要运行完成，就不会给当前续期。
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            lock.unlock();
        }
        return "hello";
    }

    //保证一定能读到最新数据，修改期间，写锁是一个排它锁(互斥锁)，读锁是一个共享锁
    //写锁没释放，读锁必须等待
    // 写 + 读 : 等待写锁释放
    // 写 + 写 : 阻塞等待
    // 读 + 写 : 有读锁。写也需要等待
    //只要有写的的存在，都必须等待
    @GetMapping("/write")
    public String writeValue(){
        //读写锁
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.writeLock();
        String s = "";
        //1.改数据加写锁，读数据加读锁
        rLock.lock();
        try {
            s = UUID.randomUUID().toString();
            Thread.sleep(10000);
            redisTemplate.opsForValue().set("writeValue", s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }

    @GetMapping("/read")
    @ResponseBody
    public String readValue() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = lock.readLock();
        String s = "";
        //读数据加读锁
        rLock.lock();
        try {
            s = redisTemplate.opsForValue().get("writeValue");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }

    /**
     * 放假，锁门
     * 1班没人，
     * 5个班全部走完，我们可以锁大门
     */
    @GetMapping("/locKDoor")
    @ResponseBody
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(5);
        door.await(); //等待闭锁完成

        return "放假了....";
    }

    @GetMapping("/gogo/{id}")
    @ResponseBody
    public String gogo(@PathVariable("id") Long id){
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown(); //计数减1
        return id+"班的人都走了";
    }

    /**
     * 车库停车
     * 尝试获取车位 [信号量]
     * 信号量:也可以用作限流
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/park")
    public String park() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.acquire(); //获取一个信号。获取一个值,占一个车位 【阻塞方法】
        return "ok";
    }

    @ResponseBody
    @GetMapping("/goPark")
    public String goPark() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release(); //释放一个车位
        return "ok => 车位+1";
    }



}
