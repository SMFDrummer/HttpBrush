package smf.icdada.test;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ThreadBuilder {
    public Thread build202Thread(int userid){
        return new Req202Thread(5000,userid);
    }
}
