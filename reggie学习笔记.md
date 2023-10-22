# 学习笔记

## Lambda表达式与Page

``` java
//        构造分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
//        构造条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort);
//        执行查询
        categoryService.page(pageInfo,lambdaQueryWrapper);
```



## stream流

```java
  List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

//            对象拷贝
            BeanUtils.copyProperties(item, dishDto);

//            分类id
            Long categoryId = item.getCategoryId();

//            根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            String categoryName = category.getName();

            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());
```



## MybatisPlus分页配置类

``` java
/**
 * MybatisPlus分页配置类
 */
@Configuration
public class MybatisPlusConfig {
	//交给spring进行管理
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }

}
```



### Page类

``` java
	//Page类里的属性
	private static final long serialVersionUID = 8545996863226528798L;
    protected List<T> records;
    protected long total;
    protected long size;
    protected long current;
    protected List<OrderItem> orders;
    protected boolean optimizeCountSql;
    protected boolean isSearchCount;
    protected boolean hitCount;
    protected String countId;
    protected Long maxLimit;

```

| 参数名           | 参数类型        | 默认值 | 描述                                          |
| ---------------- | --------------- | ------ | --------------------------------------------- |
| records          | List<T>         |        | 用来存放查询出来的数据                        |
| total            | long            |        | 返回记录的总数                                |
| size             | long            | 10     | 每页显示条数                                  |
| current          | long            | 1      | 当前页                                        |
| orders           | List<OrderItem> |        | 排序字段信息                                  |
| optimizeCountSql | boolean         | true   | 自动优化 COUNT SQL                            |
| isSearchCount    | boolean         | true   | 是否进行 count 查询，设置false后不会返回total |
| hitCount         | boolean         | false  | 是否命中count缓存                             |
| countId          | String          |        |                                               |
| maxLimit         | Long            | null   | 单页分页条数限制                              |



### mp save()

``` java
mp在执行完save(entity)操作后会对entity更新
    eg：
    @Autowired
    private SetmealDishService setmealDishService;
    //    事务的注解
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
//        保存套餐的基本信息，操作setmeal，执行insert操作
//        在执行完save方法后，会更新dto，所以他有id了
        this.save(setmealDto);
//        保存套餐和菜品的关联信息，操作setmeal_dish表，执行insert操作

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }
```



## log.info语法

```
log.info("套餐信息:{}",setmealDto);
log.info("套餐信息:" + setmealDto);
```

在实体有toString()方法时，直接打印实体会自动调用toString()方法

使用{}方式时，无需将参数转为字符串，可直接打印

## 文件上传与下载

``` java
package com.oxcrane.reggie.controller;

import com.oxcrane.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件的上传和下载
 */
@Controller
@RequestMapping("/common")
@Slf4j
@ResponseBody
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();//abc.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;//dfsdfdfd.jpg

        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            //目录不存在，需要创建
            dir.mkdirs();
        }

        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
//        输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

//        输出流，通过输出流将文件写回浏览器，在浏览器里展示文件
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

//            关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

```



## 可变参数

``` java
public class Demo { 
	public static void main(String[] args) {
		System.out.println(getSum(1,2,3));
	}
	public static int getSum(int...arr){
		int sum = 0;
		for (int i : arr) {
			sum+=i;
		}
		return sum;
	}
}
```



## SpringBoot获取参数

### @RequestBody

@RequestBody绑定的是一个对象实体，前端发送json数据，后端通过注解@RequestBody可以直接将Json转为实体对象

``` java
@Controller
@RequestMapping(value = "user/login")
@ResponseBody
// 将ajax（datas）发出的请求写入 User 对象中
public User login(@RequestBody User user) {   
// 这样就不会再被解析为跳转路径，而是直接将user对象写入 HTTP 响应正文中
    return user;    
}
```



### @RequestParam使用

``` java
@RequestMapping("/list1")
public String test1(int userId) {
　　return "list";
}
@RequestMapping("/list2")
public String test2(@RequestParam int userId) {
　　return "list";
}
```

 (1）不加@RequestParam前端的参数名需要和后端控制器的变量名保持一致才能生效

（2）不加@RequestParam参数为非必传，加@RequestParam写法参数为必传。但@RequestParam可以通过@RequestParam(required = false)设置为非必传。

（3）@RequestParam可以通过@RequestParam(“userId”)或者@RequestParam(value = “userId”)指定传入的参数名。（最主要的作用）

（4）@RequestParam可以通过@RequestParam(defaultValue = “0”)指定参数默认值

（5）如果接口除了前端调用还有后端RPC调用，则不能省略@RequestParam，否则RPC会找不到参数报错

（6）Get方式请求，参数放在url中时：

  (7)在学习reggie时，弹幕说基本数据类型不需要加@RequestParam，但引用需要，其中老师写的方法形参为delete(@RequestParam List<Long> ids))

### @PathVariable

请求路径参数@PathVariable

``` java
http://localhost:8080/dish/status/0?ids=1413384757047271425

    @PostMapping("/status/{status}")
    public R<String> stopSold(@RequestParam(value = "ids") List<Long> ids,@PathVariable Long status){
        log.info(ids.toString());
        log.error(String.valueOf(status));
//        dishService.stopSold(ids);
        return R.success("操作成功");
    }
```

status可以接受上述请求的0

### @ResponBody

**@ResponseBody**注解的作用是将controller的方法返回的对象通过适当的转换器转换为指定的格式之后，写入到response对象的body区，通常用来返回JSON数据或者是XML数据

**后端Controller层代码如下：**

``` java
@RequestMapping("/param")
@ResponseBody
public Map<String,Object> demo1(String name,int age){
	Map<String,Object> paramMap=new HashMap<String,Object>();
	paramMap.put("name",name);
	paramMap.put("age",age);
	return paramMap;
}
```

**前端浏览器请求为：**
http://localhost:8080/param?name=zhangsan&age=14

**之后前端返回的Json数据为**：
{“name”：zhangsan，“age”：14}



### get,post,put,delete

get、post、put、delete 是HTTP协议中的四种常见请求方法。

- get: 获取资源，通常用于请求服务器发送某个资源给客户端。
- post: 提交数据，通常用于向服务器提交数据，比如表单数据。
- put: 更新资源，通常用于向服务器更新指定资源的内容。
- delete: 删除资源，通常用于向服务器删除指定的资源。



## 处理前端传来的数据

一般使用实体或对应的参数来接收数据，**当实体无法接收全部参数时可以采用使用DTO（data transition Object）或者直接使用Map类型，Map类型直接将json对象转为key-value**

 
