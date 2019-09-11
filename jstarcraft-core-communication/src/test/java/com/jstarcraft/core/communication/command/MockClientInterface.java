package com.jstarcraft.core.communication.command;

import com.jstarcraft.core.communication.annotation.MessageCodec;
import com.jstarcraft.core.communication.annotation.CommandVariable;
import com.jstarcraft.core.communication.annotation.CommandVariable.VariableType;
import com.jstarcraft.core.communication.annotation.CommunicationCommand;
import com.jstarcraft.core.communication.annotation.CommunicationModule;
import com.jstarcraft.core.communication.annotation.CommunicationModule.ModuleSide;
import com.jstarcraft.core.communication.message.MessageFormat;

/**
 * 客户端通讯模块
 * 
 * @author Birdy
 *
 */
@CommunicationModule(code = 0x01, side = ModuleSide.CLIENT)
@MessageCodec(inputFormat = MessageFormat.JSON, outputFormat = MessageFormat.JSON)
public interface MockClientInterface {

    @CommunicationCommand(code = 0)
    public void testExecute(@CommandVariable(type = VariableType.MESSAGE_BODY) UserObject object);

}
