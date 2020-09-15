package com.jstarcraft.core.communication.command;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.csv.CsvContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.kryo.KryoContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.codec.standard.StandardContentCodec;
import com.jstarcraft.core.communication.annotation.CommandVariable;
import com.jstarcraft.core.communication.annotation.CommandVariable.VariableType;
import com.jstarcraft.core.communication.annotation.CommunicationCommand;
import com.jstarcraft.core.communication.annotation.CommunicationModule;
import com.jstarcraft.core.communication.annotation.CommunicationModule.ModuleSide;
import com.jstarcraft.core.communication.annotation.MessageCodec;
import com.jstarcraft.core.communication.exception.CommunicationDefinitionException;
import com.jstarcraft.core.communication.message.CommunicationMessage;
import com.jstarcraft.core.communication.message.MessageBody;
import com.jstarcraft.core.communication.message.MessageFormat;
import com.jstarcraft.core.communication.message.MessageHead;
import com.jstarcraft.core.communication.message.MessageTail;

public class CommandDefinitionTestCase {

    // 合法的模块接口
    @CommunicationModule(code = 0x01, side = ModuleSide.SERVER)
    @MessageCodec(inputFormat = MessageFormat.STANDARD, outputFormat = MessageFormat.STANDARD)
    interface LegalModuleInterface {

        @CommunicationCommand(code = 0)
        public void testUser();

        @CommunicationCommand(code = 1)
        public @CommandVariable UserObject getUser(@CommandVariable Long id);

        @CommunicationCommand(code = 2, input = HashMap.class, output = HashMap.class)
        public @CommandVariable UserObject createUser(@CommandVariable UserObject object);

        @CommunicationCommand(code = 3, input = HashMap.class, output = UserObject.class)
        public @CommandVariable(property = "id") Long updateUser(@CommandVariable(property = "id") Long id, @CommandVariable(property = "name") String name);

        @CommunicationCommand(code = 4)
        public @CommandVariable(type = VariableType.SESSION_ATTRIBUTE, property = "key", nullable = true) String deleteUser(@CommandVariable(type = VariableType.SESSION_ATTRIBUTE, property = "id", nullable = true) Long id);

    }

    @Test
    public void testLegalModule() throws Exception {
        Map<Integer, Type> requestInputTypes = new HashMap<>();
        Map<Integer, Type> requestContentTypes = new HashMap<>();
        Map<Integer, Object[]> requestInputValues = new HashMap<>();
        Map<Integer, Type> responseOutputTypes = new HashMap<>();
        Map<Integer, Type> responseContentTypes = new HashMap<>();
        Map<Integer, Object> responseOutputValues = new HashMap<>();

        // void, void, void, void
        requestInputTypes.put(0, void.class);
        requestContentTypes.put(0, void.class);
        responseOutputTypes.put(0, void.class);
        responseContentTypes.put(0, void.class);
        requestInputValues.put(0, new Object[] {});
        responseOutputValues.put(0, null);

        // long, long, user, user
        requestInputTypes.put(1, Long.class);
        requestContentTypes.put(1, Long.class);
        responseOutputTypes.put(1, UserObject.class);
        responseContentTypes.put(1, UserObject.class);
        requestInputValues.put(1, new Object[] { new Long(1) });
        responseOutputValues.put(1, UserObject.instanceOf(1, "Birdy"));

        // user, map, user, map
        requestInputTypes.put(2, UserObject.class);
        requestContentTypes.put(2, HashMap.class);
        responseOutputTypes.put(2, UserObject.class);
        responseContentTypes.put(2, HashMap.class);
        requestInputValues.put(2, new Object[] { UserObject.instanceOf(1, "Birdy") });
        responseOutputValues.put(2, UserObject.instanceOf(2, "Mickey"));

        // map, map, user, user
        requestInputTypes.put(3, HashMap.class);
        requestContentTypes.put(3, HashMap.class);
        responseOutputTypes.put(3, UserObject.class);
        responseContentTypes.put(3, UserObject.class);
        requestInputValues.put(3, new Object[] { new Long(3), new String("Birdy") });
        responseOutputValues.put(3, new Long(3));

        // void, void, void, void
        requestInputTypes.put(4, void.class);
        requestContentTypes.put(4, void.class);
        responseOutputTypes.put(4, void.class);
        responseContentTypes.put(4, void.class);
        requestInputValues.put(4, new Object[] { null });
        responseOutputValues.put(4, null);

        // TODO 此处测试应该调整
        Collection<Type> protocolClasses = new LinkedList<>();
        protocolClasses.add(UserObject.class);
        protocolClasses.add(HashMap.class);
        protocolClasses.add(LinkedHashMap.class);
        CodecDefinition protocolDefinition = CodecDefinition.instanceOf(protocolClasses);

        Map<Byte, ContentCodec> codecs = new HashMap<>();
        CsvContentCodec csvContentCodec = new CsvContentCodec(protocolDefinition);
        codecs.put(MessageFormat.CSV.getMark(), csvContentCodec);
        JsonContentCodec jsonContentCodec = new JsonContentCodec(protocolDefinition);
        codecs.put(MessageFormat.JSON.getMark(), jsonContentCodec);
        KryoContentCodec kyroContentCodec = new KryoContentCodec(protocolDefinition);
        codecs.put(MessageFormat.KRYO.getMark(), kyroContentCodec);
        StandardContentCodec protocolContentCodec = new StandardContentCodec(protocolDefinition);
        codecs.put(MessageFormat.STANDARD.getMark(), protocolContentCodec);

        MessageCodec codec = LegalModuleInterface.class.getAnnotation(MessageCodec.class);
        for (Method method : LegalModuleInterface.class.getMethods()) {
            CommandDefinition definition = CommandDefinition.instanceOf(method);
            MessageHead head = MessageHead.instanceOf(1, definition.getCommand(), definition.getModule());
            MessageTail tail = MessageTail.instanceOf(10);
            CommunicationCommand command = method.getAnnotation(CommunicationCommand.class);
            int code = command.code();
            // 测试输入类型与内容类型以及消息体的转换
            InputDefinition inputDefinition = InputDefinition.instanceOf(method, command.input(), codec);
            Assert.assertThat(method.getName(), inputDefinition.getInputType(), CoreMatchers.equalTo(requestInputTypes.get(code)));
            Assert.assertThat(method.getName(), inputDefinition.getContentType(), CoreMatchers.equalTo(requestContentTypes.get(code)));
            MessageBody inputBody = inputDefinition.getMessageBody(codecs, requestInputValues.get(code));
            CommunicationMessage inputMessage = CommunicationMessage.instanceOf(head, inputBody, tail);
            Object[] inputValues = inputDefinition.getInputValues(codecs, inputMessage, null);
            Assert.assertThat(method.getName(), inputValues, CoreMatchers.equalTo(requestInputValues.get(code)));
            // 测试输出类型与内容类型以及消息体的转换
            OutputDefinition outputDefinition = OutputDefinition.instanceOf(method, command.output(), codec);
            Assert.assertThat(method.getName(), outputDefinition.getOutputType(), CoreMatchers.equalTo(responseOutputTypes.get(code)));
            Assert.assertThat(method.getName(), outputDefinition.getContentType(), CoreMatchers.equalTo(responseContentTypes.get(code)));
            MessageBody outputBody = outputDefinition.getMessageBody(codecs, responseOutputValues.get(code));
            CommunicationMessage outputMessage = CommunicationMessage.instanceOf(head, outputBody, tail);
            Object outputValues = outputDefinition.getOutputValue(codecs, outputMessage, null);
            Assert.assertThat(method.getName(), outputValues, CoreMatchers.equalTo(responseOutputValues.get(code)));
        }
    }

    // 非法的模块接口
    @CommunicationModule(code = 0x01, side = ModuleSide.SERVER)
    @MessageCodec(inputFormat = MessageFormat.STANDARD, outputFormat = MessageFormat.STANDARD)
    interface IllegalModuleInterface {

        // 没有参数与返回,不能指定输入类型与输出类型
        @CommunicationCommand(code = 0, input = HashMap.class, output = HashMap.class)
        public void testUser();

        // 没有消息体变量,不能指定输入类型与输出类型
        @CommunicationCommand(code = 1, input = HashMap.class, output = HashMap.class)
        public @CommandVariable(type = VariableType.SESSION_ATTRIBUTE, property = "id") long testUser(@CommandVariable(type = VariableType.SESSION_ATTRIBUTE, property = "key") String name);

        // 有局域消息体变量,必须指定输入类型与输出类型
        @CommunicationCommand(code = 2)
        public @CommandVariable(property = "result") boolean updateUser(@CommandVariable(property = "id") long id, @CommandVariable(property = "name") String name);

        // 不能有擦拭类型
        @CommunicationCommand(code = 3)
        public <T> @CommandVariable List<T> getUsers(@CommandVariable T[] ids);

        // 不能有通配类型
        @CommunicationCommand(code = 4)
        public @CommandVariable List<? extends UserObject> getUsers(@CommandVariable Class<? extends UserObject> clazz);

        // 不能有接口类型
        @CommunicationCommand(code = 5)
        public @CommandVariable List<UserObject> getUsers(@CommandVariable List<UserObject> users);

        // 不能有接口类型
        @CommunicationCommand(code = 5, input = List.class, output = List.class)
        public @CommandVariable LinkedList<UserObject> getUsers(@CommandVariable LinkedList<UserObject> users);

    }

    // 非法的模块类
    @CommunicationModule(code = 0x01, side = ModuleSide.SERVER)
    @MessageCodec(inputFormat = MessageFormat.STANDARD, outputFormat = MessageFormat.STANDARD)
    abstract class IllegalModuleClass {

        // 没有参数与返回,不能指定输入类型与输出类型
        @CommunicationCommand(code = 0, input = HashMap.class, output = HashMap.class)
        abstract void testUser();

        // 没有消息体变量,不能指定输入类型与输出类型
        @CommunicationCommand(code = 1, input = HashMap.class, output = HashMap.class)
        abstract @CommandVariable(type = VariableType.SESSION_ATTRIBUTE, property = "id") long testUser(@CommandVariable(type = VariableType.SESSION_ATTRIBUTE, property = "key") String name);

        // 有局域消息体变量,必须指定输入类型与输出类型
        @CommunicationCommand(code = 2)
        abstract @CommandVariable(property = "result") boolean updateUser(@CommandVariable(property = "id") long id, @CommandVariable(property = "name") String name);

        // 不能有擦拭类型
        @CommunicationCommand(code = 3)
        abstract <T> @CommandVariable List<T> getUsers(@CommandVariable T[] ids);

        // 不能有通配类型
        @CommunicationCommand(code = 4)
        abstract @CommandVariable List<? extends UserObject> getUsers(@CommandVariable Class<? extends UserObject> clazz);

        // 不能有接口类型
        @CommunicationCommand(code = 5)
        abstract @CommandVariable List<UserObject> getUsers(@CommandVariable List<UserObject> users);

        // 不能有接口类型
        @CommunicationCommand(code = 5, input = List.class, output = List.class)
        abstract @CommandVariable LinkedList<UserObject> getUsers(@CommandVariable LinkedList<UserObject> users);

    }

    @Test
    public void testIllegalModule() throws Exception {
        MessageCodec codec = IllegalModuleInterface.class.getAnnotation(MessageCodec.class);
        for (Method method : IllegalModuleInterface.class.getMethods()) {
            CommunicationCommand command = method.getAnnotation(CommunicationCommand.class);
            try {
                InputDefinition.instanceOf(method, command.input(), codec);
                Assert.fail();
            } catch (CommunicationDefinitionException exception) {
            }
            try {
                OutputDefinition.instanceOf(method, command.output(), codec);
                Assert.fail();
            } catch (CommunicationDefinitionException exception) {
            }
            try {
                CommandDefinition.instanceOf(method);
                Assert.fail();
            } catch (CommunicationDefinitionException exception) {
            }
        }
        for (Method method : IllegalModuleClass.class.getMethods()) {
            try {
                CommandDefinition.instanceOf(method);
                Assert.fail();
            } catch (CommunicationDefinitionException exception) {
            }
        }
    }

}
