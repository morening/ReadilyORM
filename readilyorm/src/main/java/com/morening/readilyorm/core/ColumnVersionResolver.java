package com.morening.readilyorm.core;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;

import com.morening.readilyorm.annotations.NewColumn;
import com.morening.readilyorm.annotations.ToMany;
import com.morening.readilyorm.annotations.ToOne;
import com.morening.readilyorm.exception.IllegalParameterException;
import com.morening.readilyorm.util.Logger;
import com.morening.readilyorm.util.PrimitiveTypes;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by morening on 2018/9/15.
 */
final public class ColumnVersionResolver {

    private ColumnVersionCache cache = null;

    public ColumnVersionResolver(ColumnVersionCache cache) {
        this.cache = cache;
    }

    @TargetApi(Build.VERSION_CODES.P)
    public void resolve(Class<?> type){
        Logger.d(this, String.format("[%s] start resolve", type.getSimpleName()));

        Field[] fields = type.getDeclaredFields();

        for (Field field: fields){
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            if (PrimitiveTypes.isUnsupportPrimitiveType(fieldType)){
                throw new IllegalParameterException(fieldType.getSimpleName()+" is not supported, please change to its boxing class!");
            }
            String typeInDb = PrimitiveTypes.getColumnType(fieldType);

            if (!TextUtils.isEmpty(typeInDb)){
                Logger.d(this, String.format("[%s] fieldName=%s, typeInDb=%s", type.getSimpleName(), fieldName, typeInDb));
                if (field.isAnnotationPresent(NewColumn.class)){
                    NewColumn newColumn = field.getAnnotation(NewColumn.class);
                    int version = newColumn.version();
                    cache.addColumnVersion(new ColumnVersion(type, fieldName, typeInDb, version));
                }
            } else {
                if (field.isAnnotationPresent(ToOne.class)){
                    ToOne toOne = field.getAnnotation(ToOne.class);
                    String fk = toOne.fk();
                    Logger.d(this, String.format("[%s] <ToOne> fk=%s, type=%s, fieldName=%s",
                            type.getSimpleName(), fk, fieldType.getSimpleName(), fieldName));
                    if (!TextUtils.isEmpty(fk)){

                        resolve(fieldType);
                    }
                } else if (field.isAnnotationPresent(ToMany.class)){
                    ToMany toMany = field.getAnnotation(ToMany.class);
                    String rk = toMany.rk();
                    Class<?> genericType;
                    if (fieldType == List.class){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                            String typeName = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0].getTypeName();
                            try {
                                genericType = Class.forName(typeName);
                            } catch (ClassNotFoundException e) {
                                throw new IllegalParameterException(e.getMessage());
                            }
                        } else {
                            genericType = toMany.type();
                        }
                    } else {
                        /*genericType = genericType.getComponentType();*/
                        throw new IllegalParameterException("ToMany annotation only support List");
                    }
                    Logger.d(this, String.format("[%s] <ToMany> rk=%s, genericType=%s",
                            type.getSimpleName(), rk, genericType.getSimpleName()));
                    if (!TextUtils.isEmpty(rk)){

                        resolve(genericType);
                    }
                }
            }
        }
    }

}
