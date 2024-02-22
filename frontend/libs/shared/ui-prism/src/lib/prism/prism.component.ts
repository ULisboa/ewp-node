import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, ElementRef, Input, OnChanges, ViewChild } from '@angular/core';

import * as Prism from 'prismjs';

import 'prismjs/plugins/line-highlight/prism-line-highlight';
import 'prismjs/plugins/line-numbers/prism-line-numbers';
import 'prismjs/components/prism-markup';

@Component({
  standalone: true,
  imports: [CommonModule],
  selector: 'lib-prism',
  templateUrl: './prism.component.html',
  styleUrl: './prism.component.css',
})
export class PrismComponent implements AfterViewInit, OnChanges {

  @Input() 
  code?: string;

  @Input() 
  language?: string;

  @Input()
  linesToHighlight = '';

  @ViewChild('codeElem') 
  codeElem!: ElementRef;

  constructor() { }
  ngAfterViewInit() {
    Prism.highlightElement(this.codeElem.nativeElement);
  }
  ngOnChanges(changes: any): void {
    if (changes?.code) {
      if (this.codeElem?.nativeElement) {
        this.codeElem.nativeElement.textContent = this.code;
        Prism.highlightElement(this.codeElem.nativeElement);
      }
    }
  }
}
