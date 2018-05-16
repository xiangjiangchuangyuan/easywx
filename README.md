# easywx
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/xiangjiangchuangyuan/easywx/blob/master/LICENSE)
![Jar Size](https://img.shields.io/badge/jar--size-19.48k-blue.svg)

### 运行条件
* JDK(1.7+)

### 工具包特色
* 单例模式，启动时注册即可
* 对应部分常用公众号和支付接口
* 使用简单

### 使用
```
//注册公众号
WXUtil.register("appId", "appSecret", false);
//注册支付
WXUtil.registerPay("mchId", "加密的key", "你的微信回调处理完整链接");

//使用方式
//获取token
WXUtil.get().getAccessToken();
//生成微信支付预付单
WXUtil.post().createUnifiedOrder(Unifiedorder.test());
```

### 反馈
* [提交issue](https://github.com/xiangjiangchuangyuan/easywx/issues/new)
* 交流群616698275 答案easy
* email：441430565@qq.com