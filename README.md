#**share**
毕业设计：Android软件，分享，基于位置用户主动分享的社交应用。下面是问题的解决思路以及想法

_ _ _

1.应用具有引导页面，第一次使用有引导页面，更强大的引导没做 ，~~图片质量也..有点低~~

2.主界面使用自定义的viewpager，原生的viewpager存在处理触摸事件、多次滚动的bug。使用viewpager可以进行左右滑动，下方没有显示tab，跟主流有点不同，但是有一个浮动的选项，可以考虑在滚动的时候隐藏，添加一个滑动监听，感觉会高大上一点。

~~3.使用具有动画的listview,其中图片点击放大,并且可以缩放,添加瀑布流view~~。已经不是用具有动画的listview，普通listview，在手机上看不到具体效果

4.使用Bmob移动后端服务器，作为数据存储的服务器；以及Bmob的推送消息。bmob的推送通知功能好像又不行了，要不考虑使用其他推送，例如极光，集成简单。

5.使用百度地图定位，点击位置信息可以查看具体的位置，使用POI功能，进行查找附近的位置或者用户直接搜索关键字。分享的信息点击位置信息显示在百度地图上，并且可以点击导航，在手机上安装的地图软件上显示。

###### 使用的是百度地图，那么对于其他的图的坐标不一定能对应，解决办法：
- 	使用百度地图进行导航。只是显示一个坐标，有时候不能获取坐标上的位置，需要手动选择，点击，进而才能显示具体位置，最后才能进行导航，导航最好使用百度地图，因为坐标之间的切换问题。
-   百度地图可以实现启动浏览器显示地图，但是在浏览器上显示地图体验差，感觉还不如不做，可以考虑在用户点击导航的时候提示该应用是使用百度地图进行定位，使用其他地图进行导航会存在一定的误差。

6.自定义控件，例如卫星式布局，就是viewground的一些视觉动画

7.~~使用Mob的短信验证，注册用户、找回密码~~，准备对注册进行修改，使用bmob的邮箱验证或者是手机短信进行验证。

8.网络通信使用json格式。使用gson进行数据解析，使用Google开源的volley进行同行，并且对于https通信，采用自签名的方式进行验证

9.使用sqlite进行本地数据缓存。现在使用了Acache进行一些数据缓存，还有sharedPreference

10.使用多线程技术，android主线程进行控件的实例化，启动子线程进行获取数据、数据解析

11.使用正确的activity声明周期管理，节约资源，优化性能。application使用单例模式

12.使用多套图标，适配主流屏幕，使用一些视觉效果、动画效果，提高用户体验

13.使用ListView进行列表数据展示，recycleview作为瀑布流进行显示

14.添加小图放大动画效果，大图缩小的效果，手势缩放图片、双击放大/缩小、左右滑动切换图片。通过设置activity的出现与消失动画，并且设置透明主题

15.使用gridview显示等长宽的图片，但是在listview显示的时候，存在计算延迟，考虑进行自定义控件，在gridview的adapter进行数据显示、计算宽高有点浪费，应该使用其他更节约资源的方法，看了其他应用是使用自定义控件，简单的控件进行图片的显示，增加点击监听就好。
-	想到使用relativeLayout进行自定义，但是需要计算宽高，看高的计算可以使用一次计算，接着放在本地，每次使用就读取，读取也是放在内存中直接进行使用，不需要重新计算，更快，更节约资源，考虑怎么实现这个控件。

16.分享信息到其他平台，使用系统的应用，通过Intent启动。分享图片 --> 下载图片 --> intent启动应用分享 ，分享文件可以直接传递

17.图片上传不能直接使用大图，需要通过图片压缩算法，将大图压缩成适合在移动设备上传输的图片。[图片压缩库](https://github.com/mtnwrw/pdqimage)，尚未引用
_ _ _

关键技术：gson，volley，~~litepal（sqlite3）~~，Acache，UIL（universal image loader），百度LBS

