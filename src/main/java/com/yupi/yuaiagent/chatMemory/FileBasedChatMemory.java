package com.yupi.yuaiagent.chatMemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *  完成持久化的增删查功能
 *  主要是file的序列化和反序列
 */
@Slf4j
public class FileBasedChatMemory implements ChatMemory {

    private final String BASE_DIR;
    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false); // 不强制注册类，Kryo 会动态处理未注册类，方便快速序列化复杂对象
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy()); // 允许 Kryo 创建没有无参构造函数的对象，保证接口/抽象类及其子类都能正确序列化/反序列化。
    }

    //初始化
    public FileBasedChatMemory(String dir) {
        this.BASE_DIR = dir;
        File baseDir = new File(dir);
        if(!baseDir.exists()){ //创建目录
            baseDir.mkdirs();
        }
    }

    //获取文件对象
    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }

    //获取数据
    private List<Message> getConversation(String conversationId) {
        File file = getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();//拿存储的数据
        if(file.exists()){
            try(Input input = new Input(new FileInputStream(file))){
                messages = kryo.readObject(input, ArrayList.class);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        return messages;
    }

    //保存会话
    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        try(Output output = new Output(new FileOutputStream(file))){// 为空会自己创建新文件
            kryo.writeObject(output, messages);
        }catch (FileNotFoundException e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> conversationMsg =  getConversation(conversationId);
        conversationMsg.addAll(messages);
        saveConversation(conversationId, conversationMsg);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> conversationMsg = getConversation(conversationId);
        //返回后n条
        return conversationMsg.stream().skip(Math.max(0, conversationMsg.size() - lastN)).toList();
    }

    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if(file.exists()){
            file.delete();
        }
    }
}
