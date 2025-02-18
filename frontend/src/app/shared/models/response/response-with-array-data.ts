import { Type, Exclude } from 'class-transformer';
import { AdminApiResponse } from "./response";

// @dynamic (https://github.com/ng-packagr/ng-packagr/issues/696)
export class AdminApiResponseWithArrayData<D> extends AdminApiResponse {
    @Exclude()
    dataType: new () => D;
  
    @Type(type => {
      if (!type) {
        return () => null;
      }
      return (type.newObject as AdminApiResponseWithArrayData<D>).dataType;
    })
    data!: D[];
  
    constructor(dataType: new () => D) {
      super();
      this.dataType = dataType;
    }
  }
  