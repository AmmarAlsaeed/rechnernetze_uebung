# -*- coding: utf-8 -*-

import argparse
import os
import subprocess

import matplotlib.pyplot as plt
import matplotlib.gridspec as gridspec
import matplotlib.animation as animation

host0_data = []
host1_data = []
router2_ingress = []
router2_egress_queue = []
router2_egress = []
router2_egress_queue_d = []


def update_data(num, data_input, host0, host0_plt, host1, host1_plt, router_ingress, router_egress_queue, router_egress,
                router_egress_queue_d):
    for line in data_input:
        line = line.decode('utf-8')
        if line.startswith('[0] Current data rate'):
            host0_data.append(int(line.split(':')[1].strip()))
            host0.set_data(list(range(len(host0_data))), host0_data)
        if line.startswith('[0] Choke packet received'):
            host0_plt.axvline(x=int(line.split(':')[1].strip()), color='red', linewidth=0.75, zorder=1)
        if line.startswith('[1] Choke packet received'):
            host1_plt.axvline(x=int(line.split(':')[1].strip()), color='red', linewidth=0.75, zorder=1)
        if line.startswith('[1] Current data rate'):
            host1_data.append(int(line.split(':')[1].strip()))
            host1.set_data(list(range(len(host1_data))), host1_data)
        if line.startswith('[5] Ingress data rate'):
            router2_ingress.append(int(line.split(':')[1].strip()))
            router_ingress.set_data(list(range(len(router2_ingress))), router2_ingress)
        if line.startswith('[5][2] Egress queue size'):
            router2_egress_queue.append(int(line.split(':')[1].strip()))
            router_egress_queue.set_data(list(range(len(router2_egress_queue))), router2_egress_queue)
        if line.startswith('[5][2] Egress d'):
            router2_egress_queue_d.append(float(line.split(':')[1].strip()))
            router_egress_queue_d.set_data(list(range(len(router2_egress_queue_d))), router2_egress_queue_d)
        if line.startswith('[5] Egress data rate'):
            router2_egress.append(int(line.split(':')[1].strip()))
            router_egress.set_data(list(range(len(router2_egress))), router2_egress)
        if '=' in line:
            break

    return


def main(args):
    os.chdir(args.project_root)
    proc = subprocess.Popen([args.java_path, 'ipvs.RNI.Main'], stdout=subprocess.PIPE)

    plt.style.use('seaborn')
    fig = plt.figure()
    outer = gridspec.GridSpec(3, 1, hspace=0.3)

    # fig, axarr = plt.subplots(3, 1, sharex=True)
    host_grid = gridspec.GridSpecFromSubplotSpec(1, 1, subplot_spec=outer[0])
    host0_plt = plt.Subplot(fig, host_grid[0])
    host0_plt.set_title('Aktuelle Datenrate (Host 0)')
    host0, = host0_plt.plot([], [])
    host0_plt.axhline(y=30000, color='red', zorder=1)
    host0_plt.set_xlim(0, 500)
    host0_plt.set_xlabel('Zeit')
    host0_plt.set_ylim(0, 31000)
    host0_plt.set_ylabel('Datenrate')
    fig.add_subplot(host0_plt)

    host_grid = gridspec.GridSpecFromSubplotSpec(1, 1, subplot_spec=outer[1])
    host1_plt = plt.Subplot(fig, host_grid[0])
    host1_plt.set_title('Aktuelle Datenrate (Host 1)')
    host1, = host1_plt.plot([], [])
    host1_plt.axhline(y=30000, color='red', zorder=1)
    host1_plt.set_xlim(0, 500)
    host1_plt.set_xlabel('Zeit')
    host1_plt.set_ylim(0, 31000)
    host1_plt.set_ylabel('Datenrate')
    fig.add_subplot(host1_plt)

    router_grid = gridspec.GridSpecFromSubplotSpec(1, 3, subplot_spec=outer[2])

    router_ingress_plt = plt.Subplot(fig, router_grid[0])
    router_ingress_plt.set_title('Ingress Datenrate (Router 2)')
    router_ingress, = router_ingress_plt.plot([], [], linewidth=1.0)
    router_ingress_plt.set_xlim(0, 500)
    router_ingress_plt.set_xlabel('Zeit')
    router_ingress_plt.set_ylim(0, 61000)
    router_ingress_plt.set_ylabel('Datenrate')
    fig.add_subplot(router_ingress_plt)

    router_egress_queue_plt = plt.Subplot(fig, router_grid[1])
    router_egress_queue_plt.set_title('Egress Queue (Router 2)')
    router_egress_queue, = router_egress_queue_plt.plot([], [], linewidth=1.0)
    router_egress_queue_d, = router_egress_queue_plt.plot([], [], linewidth=1.0)
    router_egress_queue_plt.axhline(y=100000, color='red', zorder=1)
    router_egress_queue_plt.set_xlim(0, 500)
    router_egress_queue_plt.set_xlabel('Zeit')
    router_egress_queue_plt.set_ylim(0, 500000)
    router_egress_queue_plt.set_ylabel('Queue Länge')
    fig.add_subplot(router_egress_queue_plt)

    router_egress_plt = plt.Subplot(fig, router_grid[2])
    router_egress_plt.set_title('Egress Datenrate (Router 2)')
    router_egress, = router_egress_plt.plot([], [], linewidth=1.0)
    router_egress_plt.axhline(y=30000, color='red', zorder=1)
    router_egress_plt.set_xlim(0, 500)
    router_egress_plt.set_xlabel('Zeit')
    router_egress_plt.set_ylim(0, 31000)
    router_egress_plt.set_ylabel('Datenrate')
    fig.add_subplot(router_egress_plt)

    animated = animation.FuncAnimation(fig, update_data, fargs=(proc.stdout, host0, host0_plt, host1, host1_plt,
                                                                router_ingress, router_egress_queue, router_egress,
                                                                router_egress_queue_d), interval=125)

    mng = plt.get_current_fig_manager()
    mng.full_screen_toggle()
    plt.show()


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--java_path')
    parser.add_argument('--project_root')
    main(parser.parse_args())
