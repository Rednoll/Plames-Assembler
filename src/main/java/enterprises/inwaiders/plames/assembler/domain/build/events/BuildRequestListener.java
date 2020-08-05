package enterprises.inwaiders.plames.assembler.domain.build.events;

import enterprises.inwaiders.plames.assembler.domain.build.BuildRequest;

public interface BuildRequestListener {

	public void run(BuildRequest request);
}
