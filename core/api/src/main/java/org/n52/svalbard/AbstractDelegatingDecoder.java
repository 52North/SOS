package org.n52.svalbard;

import javax.inject.Inject;

import org.n52.svalbard.decode.Decoder;
import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.DecoderRepository;


/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class AbstractDelegatingDecoder<T, S> implements Decoder<T, S> {

    private DecoderRepository decoderRepository;

    public DecoderRepository getDecoderRepository() {
        return decoderRepository;
    }

    @Inject
    public void setDecoderRepository(DecoderRepository decoderRepository) {
        this.decoderRepository = decoderRepository;
    }

    public <T, S> Decoder<T, S> getDecoder(DecoderKey key, DecoderKey... others) {
        return this.decoderRepository.getDecoder(key, others);
    }

}
