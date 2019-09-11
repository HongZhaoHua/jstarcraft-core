package com.jstarcraft.core.communication.command;

import java.util.HashMap;

import com.jstarcraft.core.communication.annotation.CommandVariable;
import com.jstarcraft.core.communication.annotation.CommandVariable.VariableType;
import com.jstarcraft.core.communication.annotation.CommunicationCommand;
import com.jstarcraft.core.communication.annotation.CommunicationModule;
import com.jstarcraft.core.communication.annotation.CommunicationModule.ModuleSide;
import com.jstarcraft.core.communication.annotation.MessageCodec;
import com.jstarcraft.core.communication.message.MessageFormat;

/**
 * 服务端通讯模块
 * 
 * @author Birdy
 *
 */
@CommunicationModule(code = 0x01, side = ModuleSide.SERVER)
@MessageCodec(inputFormat = MessageFormat.JSON, outputFormat = MessageFormat.JSON)
public interface MockServerInterface {

    @CommunicationCommand(code = 1)
    public @CommandVariable UserObject getUser(@CommandVariable Long id);

    @CommunicationCommand(code = 2, input = HashMap.class, output = HashMap.class)
    public @CommandVariable UserObject createUser(@CommandVariable UserObject object);

    @CommunicationCommand(code = 3, input = HashMap.class, output = UserObject.class)
    public @CommandVariable(property = "name") String updateUser(@CommandVariable(property = "id") Long id, @CommandVariable(property = "name") String name);

    @CommunicationCommand(code = 4)
    public @CommandVariable(type = VariableType.SESSION_ATTRIBUTE, property = "key") String deleteUser(@CommandVariable Long id);

    @CommunicationCommand(code = 5)
    public @CommandVariable int addition(@CommandVariable int number);

    @CommunicationCommand(code = 6, input = HashMap.class)
    public @CommandVariable int addition(@CommandVariable(property = "left") int left, @CommandVariable(property = "right") int right);

    @CommunicationCommand(code = 7)
    public @CommandVariable String md5(@CommandVariable String data);

}
