import numpy as np
import matplotlib.pyplot as plt
plt.style.use('ggplot')

a = np.loadtxt("neighbours.txt")
plt.imshow(a, interpolation='nearest', aspect='auto', cmap='inferno')
plt.savefig("neighbours.pdf")


a = np.loadtxt("matrix.txt")
plt.imshow(a, interpolation='nearest', aspect='auto', cmap='inferno')
plt.savefig("matrix.pdf")


a = np.loadtxt("shower.txt")
plt.imshow(a, interpolation='nearest', aspect='auto', cmap='inferno')
plt.savefig("shower.pdf")
