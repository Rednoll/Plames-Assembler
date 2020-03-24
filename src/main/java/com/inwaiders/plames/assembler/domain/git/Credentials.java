package com.inwaiders.plames.assembler.domain.git;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public abstract class Credentials {

	public abstract UsernamePasswordCredentialsProvider getProvider();
}
