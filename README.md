# C-Compiler-Front
哈工大(威海)编译原理课程实验的程序

支持词法分析，语法分析与语义分析，提供简单的控制台界面。
语法规则支持快速修改，可以方便的调整语言特性。语法分析基于LR实现，提供SLR与LR1两种模式
语义分析可识别类新并生成三地址码。

运行过程中会将信息输出至执行文件目录下。生成的词法序列文件名为lexer-info.txt，三地址码文件为mediate-code.txt。
