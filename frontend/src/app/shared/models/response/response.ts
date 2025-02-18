import { Type } from 'class-transformer';
import { Message } from '../message/message';

// @dynamic (https://github.com/ng-packagr/ng-packagr/issues/696)
export abstract class AdminApiResponse {
    @Type(() => Message)
    messages?: Message[];
  }