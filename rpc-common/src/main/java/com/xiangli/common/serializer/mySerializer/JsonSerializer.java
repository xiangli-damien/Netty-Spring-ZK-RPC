package com.xiangli.common.serializer.mySerializer;

import com.xiangli.common.message.RpcRequest;
import com.xiangli.common.message.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/06 16:15
 */
@Slf4j
public class JsonSerializer implements Serializer{
    @Override
    public byte[] serialize(Object obj) {
        // 将对象序列化成字节数组
        byte[] bytes = JSONObject.toJSONBytes(obj);
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        // 根据消息类型进行反序列化成不同的对象
        switch (messageType){
            // 如果是RPCrequest类型的消息，就反序列化成RpcRequest对象
            case 0:
                RpcRequest request = JSON.parseObject(bytes, RpcRequest.class);
                Object[] objects = new Object[request.getParams().length];
                // 把json字串转化成对应的对象， fastjson可以读出基本数据类型，不用转化
                // 对转换后的request中的params属性逐个进行类型判断
                for(int i = 0; i < objects.length; i++){
                    Class<?> paramsType = request.getParamsType()[i];
                    //判断每个对象类型是否和paramsTypes中的一致
                    if (!paramsType.isAssignableFrom(request.getParams()[i].getClass())){
                        //如果不一致，就行进行类型转换
                        objects[i] = JSONObject.toJavaObject((JSONObject) request.getParams()[i],request.getParamsType()[i]);
                    }else{
                        //如果一致就直接赋给objects[i]
                        objects[i] = request.getParams()[i];
                    }
                }
                request.setParams(objects);
                obj = request;
                break;
            case 1:
                RpcResponse response = JSON.parseObject(bytes, RpcResponse.class);
                Class<?> dataType = response.getDataType();
                //判断转化后的response对象中的data的类型是否正确
                if (response.getData() != null && JSONObject.class.isAssignableFrom(response.getData().getClass())) {
                    // 先检查data是否为null，以及是否是JSONObject类型
                    response.setData(JSONObject.toJavaObject((JSONObject) response.getData(), dataType));
                } else {
                    log.warn("Data is null or not a JSONObject, skipping conversion.");
                }
                obj = response;
                break;
            default:
                log.error("Unsupported message type: {}", messageType);
                throw new UnsupportedOperationException("Unsupported message type: " + messageType);
        }
        return obj;
    }

    @Override
    public int getType() {
        return 1;
    }
}
