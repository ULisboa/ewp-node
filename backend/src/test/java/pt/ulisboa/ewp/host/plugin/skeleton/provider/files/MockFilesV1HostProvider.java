package pt.ulisboa.ewp.host.plugin.skeleton.provider.files;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MockFilesV1HostProvider extends FilesV1HostProvider {

  private final Map<String, FileResponse> fileIdToFileResponseMap = new HashMap<>();

  public MockFilesV1HostProvider registerFile(String fileId, FileResponse fileResponse) {
    this.fileIdToFileResponseMap.put(fileId, fileResponse);
    return this;
  }

  @Override
  public Optional<FileResponse> getFile(Collection<String> requesterCoveredHeiIds, String fileId) {
    return Optional.ofNullable(this.fileIdToFileResponseMap.get(fileId));
  }
}
