import { Type, Exclude } from 'class-transformer';
import { AdminApiResponse } from "./response";

// @dynamic (https://github.com/ng-packagr/ng-packagr/issues/696)
export class AdminApiResponseWithObjectData<D> extends AdminApiResponse {
    @Exclude()
    dataType: new () => D;
  
    @Type(type => {
      if (!type) {
        return () => null;
      }
      return (type.newObject as AdminApiResponseWithObjectData<D>).dataType;
    })
    data!: D;
  
    constructor(dataType: new () => D) {
      super();
      this.dataType = dataType;
    }
  }
  