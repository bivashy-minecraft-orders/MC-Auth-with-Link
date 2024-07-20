package me.mastercapexd.auth.link.telegram;

import com.bivashy.auth.api.link.user.info.LinkUserIdentificator;
import com.bivashy.auth.api.link.user.info.impl.UserNumberIdentificator;
import com.bivashy.lamp.telegram.TelegramActor;
import com.bivashy.lamp.telegram.dispatch.DispatchSource;
import com.bivashy.messenger.common.identificator.Identificator;
import com.bivashy.messenger.common.message.Message;
import com.bivashy.messenger.telegram.message.TelegramMessage;
import me.mastercapexd.auth.link.LinkCommandActorWrapperTemplate;

public class TelegramCommandActorWrapper extends LinkCommandActorWrapperTemplate<TelegramActor> implements TelegramActor {

    public TelegramCommandActorWrapper(TelegramActor actor) {
        super(actor);
    }

    @Override
    public void send(Message message) {
        if (!message.safeAs(TelegramMessage.class).isPresent())
            return;
        message.as(TelegramMessage.class).send(Identificator.fromObject(actor.getDispatchSource().getChatIdentficator().asObject()),
                TelegramMessage.getDefaultApiProvider(), response -> {
                    if (!response.isOk())
                        TelegramLinkType.getInstance()
                                .newMessageBuilder("Error occurred " + response.description() + ". Error code: " + response.errorCode())
                                .build()
                                .send(Identificator.fromObject(actor.getDispatchSource().getChatIdentficator().asObject()));
                });
    }

    @Override
    public LinkUserIdentificator userId() {
        return new UserNumberIdentificator(actor.getId());
    }

    @Override
    public DispatchSource getDispatchSource() {
        return actor.getDispatchSource();
    }

    @Override
    public void deleteTriggerMessage() {
        throw new UnsupportedOperationException();
    }

}
