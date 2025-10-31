package com.yupi.yuaiagent.manus.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  将 step 方法分解为 think 和 act 两个抽象方法
 */

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ReActAgent extends BaseAgent{
    public abstract boolean think();
    public abstract String act();

    @Override
    public String step(){
        try{
            boolean shouldAct = think();
            if(!shouldAct){
                return "thinking finished - no action"; //不需要调用工具就直接返回
            }
            return act();
        }catch (Exception e){
            e.printStackTrace();
            return "step failed: " + e.getMessage();
        }
    }
}
