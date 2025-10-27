# Exercise 9.1
## 9.1.1
- In Threads work, our standard deviation seems to fluctuate, but does tend towards becoming smaller overall. We have no idea why this is the case (potentially just the operating system doing something, like allocating ressources towards other processes in the system). 
- In thread

## 9.1.2
- OS:   Windows 11; 10.0; amd64
- JVM:  Microsoft; 17.0.12
- CPU:  AMD64 Family 25 Model 80 Stepping 0,  AuthenticAMD; 16 "cores"
- Date: 2025-10-27T09:19:35+0100

### Mark 7 measurements
- Point creation 17.5 ns 0.20 16777216
- Thread's work 1818.8 ns 22.87 262144
- Thread create 666.4 ns 5.47 524288
- Thread create start 95919.4 ns 766.45 4096
- Thread create start join 195074.4 ns 1358.87       2048
- ai value = 1070732704
- Uncontended lock 5.3 ns 0.03 67108864

The values are plausible because in threads work it uses 1818 ns to perform 1000 "operations", which if divided with 1000, closely resembles the single operation of point creation (noted that point creation is more expensive as we here create a new object as opposed to just incrementing a value).

The data also shows that creating a thread object is more expensive in comparison to creating a point object, which is to be expected.

In the measurements of starting a thread, the value is also around the expected value, albeit on the higher end, as we would expect the average to be more around 75000 thousand, as we saw with the measurements done with Mark 6. But there is a constant overhead in the thread start, which explains why we end up with a value of around 70000-90000 ish. 


## 9.1.3
95919.4 - 666.4 = 95253 ns 

Creation of a thread takes 666.4 ns whilst creating and starting takes 95919.4 ns, ergo starting the thread is very expensive compared to creating it. Therefore creating threads for small computations make no sense. 


# Exercise 9.2
## 9.2.1



