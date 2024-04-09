package com.english.netty.handler;

import com.english.util.InstanceUtils;
import com.english.service.CorpusService;
import com.english.service.CorpusServiceImpl;
import com.english.service.DictionaryService;
import com.english.service.DictionaryServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;

/**
 * @author XYC
 * 客户端请求处理
 */
public class MessageContentHandler extends ChannelInboundHandlerAdapter {
    public final ObjectMapper json = InstanceUtils.JSON;
    private final DictionaryService dictionaryService = DictionaryServiceImpl.DICTIONARY_SERVICE;
    private final CorpusService corpusService = CorpusServiceImpl.CORPUS_SERVICE;

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        Map<String, String> map = (Map<String, String>) msg;
        String method = map.get("method");
        if (method != null) {
            Object data = null;
            String entity = map.get("entity");
            String args = map.get("args");
            switch (entity) {
                case "dictionary": {
                    switch (method) {
                        case "queryRandom": {
                            data = dictionaryService.queryRandom(Integer.parseInt(args));
                            break;
                        }
                        case "queryRandomAddCorpus": {
                            data = dictionaryService.queryRandomAddCorpus(Integer.parseInt(args));
                            break;
                        }
                        case "translate": {
                            String[] split = args.split("&");
                            data = dictionaryService.translate(split[0], split[1], split[2]);
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    break;
                }
                case "corpus": {
                    switch (method) {
                        case "queryRandom": {
                            data = corpusService.queryRandom(Integer.parseInt(args));
                            break;
                        }
                        case "translate": {
                            String[] split = args.split("&");
                            data = corpusService.translate(split[0], split[1], split[2]);
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    break;
                }
                default: {
                    break;
                }
            }
            if (data != null) {
                String respondContent = json.writeValueAsString(data);
                context.channel().writeAndFlush(respondContent);
            }
        }
        super.channelRead(context, msg);
    }
}
