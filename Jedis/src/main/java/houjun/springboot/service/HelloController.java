package houjun.springboot.service;

import org.hyperic.sigar.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @PostMapping("")
    public void set() {
        redisTemplate.boundValueOps("hello").set("world");
    }

    @GetMapping("")
    public void get() throws Exception {
        redisTemplate.boundValueOps("hello").set("world");
        while (true) {
            cpu();
            memory();
//            net();
//            file();
            LocalDateTime now = LocalDateTime.now();
            System.out.println("时间：" + now + "; key: hello ,value: "+ redisTemplate.boundValueOps("hello").get());
            Thread.sleep(1000);
        }
    }


    private static void memory() throws SigarException {

        Sigar sigar = new Sigar();
        Mem mem = sigar.getMem();
        // 内存总量
        System.out.println("内存总量:    " + mem.getTotal() / 1024L + "K av");
        // 当前内存使用量
        System.out.println("当前内存使用量:    " + mem.getUsed() / 1024L + "K used");
        // 当前内存剩余量
        System.out.println("当前内存剩余量:    " + mem.getFree() / 1024L + "K free");
        Swap swap = sigar.getSwap();
        // 交换区总量
        System.out.println("交换区总量:    " + swap.getTotal() / 1024L + "K av");
        // 当前交换区使用量
        System.out.println("当前交换区使用量:    " + swap.getUsed() / 1024L + "K used");
        // 当前交换区剩余量
        System.out.println("当前交换区剩余量:    " + swap.getFree() / 1024L + "K free");
    }

    private static void cpu() throws SigarException {
        Sigar sigar = new Sigar();
        CpuInfo infos[] = sigar.getCpuInfoList();
        CpuPerc cpuList[] = null;
        cpuList = sigar.getCpuPercList();
        for (int i = 0; i < infos.length; i++) {// 不管是单块CPU还是多CPU都适用
            CpuInfo info = infos[i];
            System.out.println("第" + (i + 1) + "块CPU信息");
            System.out.println("CPU的总量MHz:    " + info.getMhz());// CPU的总量MHz
            System.out.println("CPU生产商:    " + info.getVendor());// 获得CPU的卖主，如：Intel
            System.out.println("CPU类别:    " + info.getModel());// 获得CPU的类别，如：Celeron
            System.out.println("CPU缓存数量:    " + info.getCacheSize());// 缓冲存储器数量
            printCpuPerc(cpuList[i]);
        }
    }

    private static void printCpuPerc(CpuPerc cpu) {
        System.out.println("CPU用户使用率:    " + CpuPerc.format(cpu.getUser()));// 用户使用率
        System.out.println("CPU系统使用率:    " + CpuPerc.format(cpu.getSys()));// 系统使用率
        System.out.println("CPU当前等待率:    " + CpuPerc.format(cpu.getWait()));// 当前等待率
        System.out.println("CPU当前错误率:    " + CpuPerc.format(cpu.getNice()));//
        System.out.println("CPU当前空闲率:    " + CpuPerc.format(cpu.getIdle()));// 当前空闲率
        System.out.println("CPU总的使用率:    " + CpuPerc.format(cpu.getCombined()));// 总的使用率
    }

    private static void net() throws Exception {
        Sigar sigar = new Sigar();
        String ifNames[] = sigar.getNetInterfaceList();
        for (int i = 0; i < ifNames.length; i++) {
            String name = ifNames[i];
            NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
            System.out.println("网络设备名:    " + name);// 网络设备名
            System.out.println("IP地址:    " + ifconfig.getAddress());// IP地址
            System.out.println("子网掩码:    " + ifconfig.getNetmask());// 子网掩码
            if ((ifconfig.getFlags() & 1L) <= 0L) {
                System.out.println("!IFF_UP...skipping getNetInterfaceStat");
                continue;
            }
            NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
            System.out.println(name + "接收的总包裹数:" + ifstat.getRxPackets());// 接收的总包裹数
            System.out.println(name + "发送的总包裹数:" + ifstat.getTxPackets());// 发送的总包裹数
            System.out.println(name + "接收到的总字节数:" + ifstat.getRxBytes());// 接收到的总字节数
            System.out.println(name + "发送的总字节数:" + ifstat.getTxBytes());// 发送的总字节数
            System.out.println(name + "接收到的错误包数:" + ifstat.getRxErrors());// 接收到的错误包数
            System.out.println(name + "发送数据包时的错误数:" + ifstat.getTxErrors());// 发送数据包时的错误数
            System.out.println(name + "接收时丢弃的包数:" + ifstat.getRxDropped());// 接收时丢弃的包数
            System.out.println(name + "发送时丢弃的包数:" + ifstat.getTxDropped());// 发送时丢弃的包数
        }
    }

    private static void file() throws Exception {
        Sigar sigar = new Sigar();
        FileSystem fslist[] = sigar.getFileSystemList();
        for (int i = 0; i < fslist.length; i++) {
            System.out.println("分区的盘符名称" + i);
            FileSystem fs = fslist[i];
            // 分区的盘符名称
            System.out.println("盘符名称:    " + fs.getDevName());
            // 分区的盘符名称
            System.out.println("盘符路径:    " + fs.getDirName());
            System.out.println("盘符标志:    " + fs.getFlags());//
            // 文件系统类型，比如 FAT32、NTFS
            System.out.println("盘符类型:    " + fs.getSysTypeName());
            // 文件系统类型名，比如本地硬盘、光驱、网络文件系统等
            System.out.println("盘符类型名:    " + fs.getTypeName());
            // 文件系统类型
            System.out.println("盘符文件系统类型:    " + fs.getType());
            FileSystemUsage usage = null;
            usage = sigar.getFileSystemUsage(fs.getDirName());
            switch (fs.getType()) {
                case 0: // TYPE_UNKNOWN ：未知
                    break;
                case 1: // TYPE_NONE
                    break;
                case 2: // TYPE_LOCAL_DISK : 本地硬盘
                    // 文件系统总大小
                    System.out.println(fs.getDevName() + "总大小:    " + usage.getTotal() + "KB");
                    // 文件系统剩余大小
                    System.out.println(fs.getDevName() + "剩余大小:    " + usage.getFree() + "KB");
                    // 文件系统可用大小
                    System.out.println(fs.getDevName() + "可用大小:    " + usage.getAvail() + "KB");
                    // 文件系统已经使用量
                    System.out.println(fs.getDevName() + "已经使用量:    " + usage.getUsed() + "KB");
                    double usePercent = usage.getUsePercent() * 100D;
                    // 文件系统资源的利用率
                    System.out.println(fs.getDevName() + "资源的利用率:    " + usePercent + "%");
                    break;
                case 3:// TYPE_NETWORK ：网络
                    break;
                case 4:// TYPE_RAM_DISK ：闪存
                    break;
                case 5:// TYPE_CDROM ：光驱
                    break;
                case 6:// TYPE_SWAP ：页面交换
                    break;
            }
            System.out.println(fs.getDevName() + "读出：    " + usage.getDiskReads());
            System.out.println(fs.getDevName() + "写入：    " + usage.getDiskWrites());
        }
        return;
    }


    public static void main(String[] args) throws SigarException {
        System.out.println(System.getProperty("java.library.path"));
        // cpu信息
        cpu();
        System.out.println("----------------------------------");
        // 内存信息
        memory();
        System.out.println("----------------------------------");

    }

}
