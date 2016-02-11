import numpy as np
import matplotlib.pyplot as plt
plt.style.use('ggplot')

a = np.loadtxt("matrix.txt")
plt.imshow(a, interpolation='nearest', aspect='auto', cmap='inferno')
plt.savefig("matrix.pdf")