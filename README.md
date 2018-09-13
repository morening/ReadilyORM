# ReadilyORM
 * ReadilyORM is a easy, convenient, lightweight ORM.
 * All operations can rollback if failed when processing.
 * ReadilyORM could support from api 16(Android JellyBean) to api 28(Android Pie), it's that almost meet 99% device developing requirements.
 
## How to use
* For Maven, plz refer to the below
 ```xml
 <dependency>
   <groupId>com.morening.readilyorm</groupId>
   <artifactId>readilyorm</artifactId>
   <version>0.1.2</version>
   <type>pom</type>
 </dependency>
 ```
* For Gradle, plz introduce like below
 ```groovy
 implement 'com.morening.readilyorm:readilyorm:0.1.2'
 ```
* If using Ivy, have to add dependency like below
 ```xml
 <dependency org='com.morening.readilyorm' name='readilyorm' rev='0.1.2'>
   <artifact name='readilyorm' ext='pom' ></artifact>
 </dependency>
 ```
 
## Some Constraints
 1. ReadilyORM support multi-model and every model will create a table to restore all model's datas.
 2. Every model's table is associated by foreign key when using annotations.
 3. Must create corresponding field when using ToMany/ToOne. Below is a example, More details, Please check AndroidTest.
 ```java
 // In CustomerBean, there are some OrderBean's instance
 @ToMany(rk = "rk_customer_id", type = OrderBean.class)
 private List<OrderBean> orders;
 ```
 ```java
 // In OrderBean, it will keep customerbean's id
 private Integer rk_customer_id;
 ```
 ```java
 // And OrderBean keeps a LocationBean instance, so need to create a foreign key of LocationBean
 @ToOne(fk = "fk_location_id")
 private LocationBean location;
 ```
  ```java
  // In OrderBean, it will keep locationbean's id
  private Integer fk_location_id;
  ```
 4. Please don't modify any column name in database or upgrade database's version and implement DatabaseVersionChangedListener.
 5. By default, primary key is Integer type and auto increment.
 6. ReadilyORM don't support unboxing type, for example int/long/float and so on.
 7. Insert method will ignore the id value.
 8. If the parameter's fields are null, you will get all datas when retrieving.
 9. Updating depends on primary key, so model's id must not be null.
 10. Delete all if the parameter's fields are null.
 
## Simple Demo
This is a catering booking example, Customer has some attributes including name, age and some order records and so one, and every order has corresponding location.
```java
// Customer represented like below
public class CustomerBean {
    
    private Integer id;
    
    @NotNull
    private String name;
    
    @ToMany(rk = "rk_customerbean_id", type = OrderBean.class)
    private List<OrderBean> orderBeans;
}
```
```java
// Order represented like below
public class OrderBean {
    
    private Integer id;
    
    private String price;
    
    @ToOne(fk = "fk_locationbean_id")
    private LocationBean locationBean;
    
    private Integer rk_customerbean_id;
    
    private Integer fk_locationbean_id;
}
```
```java
// Location represented like below
public class LocationBean {
    
    private Integer id;
    
    private String address;
}
```
Now let's save the information including Customer, Order and Location.
```java
// Create a ReadilyORM instance
ReadilyORM readilyORM = new ReadilyORM.Builder(getContext())
.name("demo").version(1)
.type(CustomerBean.class).build();
```
 ```java
// Insert CustomerBean like below
// Because CustomerBean is consist of OrderBean and OrderBean's LocationBean, so just insert CustomerBean,
// OrderBean and its LocationBean will be inserted by default.
// To be convenient, the method will return the inserted instance's list.
List<CustomerBean> customerbeans = readilyORM.insert(customerBean);
```
```java
// Retrieve the information
// The parameter must be a CustomerBean's instance if want CustomerBean's information.
// If want all CustomerBeans, could set a empty instance, it means all fields should be null.
// If need some specific CustomerBeans, have to set field's value.
 List<CustomerBean> customerBeans = readilyORM.retrieve(new CustomerBean());
```
```java
// Update CustomerBean
// Because in ReadilyORM, all Objects are associated with object's id,
// the updating CustomerBean need a correct id.
 List<CustomerBean> customerBeans = readilyORM.update(customerBean);
```
```java
// Delete CustomerBean
// Same with the retrieve operation, delete operation could delete the specific object or all.
// ReadilyORM will delete all CustomerBean objects from database like below.
List<CustomerBean> customerBeans = readilyORM.delete(new CustomerBean());
```
 
## License
 Copyright 2018 morening

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
