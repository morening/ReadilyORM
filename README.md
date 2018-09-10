# ReadilyORM
 * ReadilyORM is a easy, convenient, lightweight ORM.
 * All operations can rollback if failed when processing.
 * ReadilyORM could support from api 16(Android JellyBean) to api 28(Android Pie), it's that almost meet 99% device developing requirements.
 
 ## How to use
 
 
 ## Some Constraints
 1. ReadilyORM support multi-model and every model will create a table to restore all model's datas.
 2. Every model's table is associated by foreign key when using annotations.
 3. Must create corresponding field when using ToMany/ToOne. Below is a example, More details, Please check AndroidTest.
````java
// In CustomerBean, there are some OrderBean's instance
@ToMany(rk = "rk_customer_id", type = OrderBean.class)
private List<OrderBean> orders;
````
````java
// In OrderBean, it will keep customerbean's id
private Integer rk_customer_id;
````
````java
// And OrderBean keeps a LocationBean instance, so need to create a foreign key of LocationBean
@ToOne(fk = "fk_location_id")
private LocationBean location;
````
 4. Please don't modify any column name in database or upgrade database's version and implement DatabaseVersionChangedListener.
 5. By default, primary key is Integer type and auto increment.
 6. ReadilyORM don't support unboxing type, for example int/long/float and so on.
 7. Insert method will ignore the id value.
 8. If the parameter's fields are null, you will get all datas when retrieving.
 9. Updating depends on primary key, so model's id must not be null.
 10. Delete all if the parameter's fields are null.
 
 
 ## Copy Right