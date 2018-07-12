package com.light.io;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.InetAddress;

@NoArgsConstructor
@Setter
@Getter
public class ServerContext {

    private InetAddress ip;
    private int port;
}
